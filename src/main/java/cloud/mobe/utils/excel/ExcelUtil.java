package cloud.mobe.utils.excel;

import static cloud.mobe.utils.CheckEmptyUtil.isEmpty;
import static cloud.mobe.utils.CheckEmptyUtil.isNotEmpty;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.EMPTY_LIST;

import cloud.mobe.utils.excel.annotation.ExcelColumn;
import cloud.mobe.utils.excel.annotation.ExcelFile;
import cloud.mobe.utils.excel.annotation.ExcelHeader;
import cloud.mobe.utils.excel.dto.ExcelItemAnnotationInfo;
import cloud.mobe.utils.excel.dto.ExcelStructureInfo;
import cloud.mobe.utils.excel.dto.ExcelStructureInfo.DataField;
import cloud.mobe.utils.excel.dto.ExcelStructureInfo.HeaderField;
import cloud.mobe.utils.exception.MobeServiceException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel文档工具.
 *
 * @author lsw1991abc@gmail.com
 * @since 2020/4/13 22:45
 */
@Slf4j
public class ExcelUtil {

  /**
   * 从文件构建workbook.
   *
   * @param file 传入的文件，如果文件是空，数据则为空
   * @param definitionClass 传入文件对应的Class实体映射
   * @return 构建的表格信息
   * @throws IOException 输入流的异常
   */
  public static <T extends AbstractExcelDefinition> ExcelDetail<T> build(
      MultipartFile file, Class<T> definitionClass) throws IOException {
    ExcelDetail<T> excelDetail = build(definitionClass);

    Workbook workbook;
    if (file != null) {
      String fileName = file.getOriginalFilename();
      fileName = isEmpty(fileName) ? "" : fileName.trim();
      excelDetail.setFileName(fileName);
      String fileNameLower = fileName.toLowerCase();

      try {
        if (fileNameLower.endsWith(".xlsx")) {
          workbook = new XSSFWorkbook(file.getInputStream());
        } else if (fileNameLower.endsWith(".xls")) {
          workbook = new HSSFWorkbook(file.getInputStream());
        } else {
          workbook = new XSSFWorkbook();
          log.warn("未知的文件类型，文件名 - {}", fileName);
        }
      } catch (OfficeXmlFileException e) {
        log.warn("文件类型错误 - {}", e.getMessage(), e);
        throw new MobeServiceException("文件后缀和内容不统一");
      }
    } else {
      workbook = new XSSFWorkbook();
      excelDetail.setFileName("");
      log.warn("读入文件流为空");
    }
    excelDetail.setWorkbook(workbook);
    return excelDetail;
  }

  /**
   * 构建excel信息.
   *
   * @param definitionClass 定义的类映射
   * @return excel信息
   */
  private static <T extends AbstractExcelDefinition> ExcelDetail<T> build(
      Class<T> definitionClass) {
    ExcelDetail<T> excelDetail = new ExcelDetail<T>();
    if (definitionClass == null) {
      return excelDetail;
    }
    excelDetail.setDefinitionClass(definitionClass);

    ExcelFile excelFile = definitionClass.getAnnotation(ExcelFile.class);
    excelDetail.setFileName(excelFile.value() + "-示例.xlsx");
    excelDetail.setSheetName(excelFile.sheet());
    // 头信息占用的行数
    int rowNum = excelFile.headerIndex();
    if (rowNum < 1) {
      throw new MobeServiceException("[ExcelFile.headerIndex()]表格头位置不能小于1");
    }
    excelDetail.setRowNum(excelFile.headerIndex());

    Field[] declaredFields = definitionClass.getDeclaredFields();

    List<Object[]> annotationFields = newArrayListWithExpectedSize(declaredFields.length);
    for (Field declaredField : declaredFields) {
      ExcelColumn fieldAnnotation = declaredField.getAnnotation(ExcelColumn.class);
      if (fieldAnnotation == null) {
        continue;
      }
      try {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(declaredField.getName(),
            definitionClass);
        annotationFields.add(new Object[]{fieldAnnotation.index(),
            propertyDescriptor.getWriteMethod(), fieldAnnotation.name(), declaredField.getType()});
      } catch (IntrospectionException e) {
        throw new MobeServiceException("未找到字段", e);
      }
    }
    excelDetail.setAnnotationFields(annotationFields);
    return excelDetail;
  }

  /**
   * 获取单元格数据.
   *
   * @param cell 单元格.
   * @param clz 实体类的类型
   * @return 单元格数值
   */
  public static Object getCellValue(Cell cell, Class<?> clz) {
    Object cellValue = null;
    // excel单元格数据类型
    try {
      CellType cellType = cell.getCellType();
      if (cellType == CellType.NUMERIC) {
        if (DateUtil.isCellDateFormatted(cell)) {
          cellValue = String.valueOf(cell.getDateCellValue());
        } else {
          cell.setCellType(CellType.STRING);
          String tempCellValue = cell.getStringCellValue();
          if (tempCellValue.contains(".")) {
            cellValue = String.valueOf(Double.valueOf(tempCellValue)).trim();
          } else {
            cellValue = tempCellValue.trim();
          }
        }
      } else if (cellType == CellType.STRING) {
        cellValue = cell.getStringCellValue();
      } else if (cellType == CellType.FORMULA) {
        cellValue = cell.getStringCellValue();
      } else if (cellType == CellType.BOOLEAN) {
        cellValue = cell.getBooleanCellValue();
      } else if (cellType == CellType.ERROR) {
        cellValue = cell.getErrorCellValue();
      }
      if (cellValue == null) {
        return null;
      }

      // 实体类的数据类型
      if (clz == String.class) {
        cellValue = String.valueOf(cellValue.toString());
      } else if (clz == Integer.class) {
        cellValue = Double.valueOf(cellValue.toString()).intValue();
      } else if (clz == Long.class) {
        cellValue = Double.valueOf(cellValue.toString()).longValue();
      } else if (clz == Double.class) {
        cellValue = Double.valueOf(cellValue.toString());
      } else if (clz == Float.class) {
        cellValue = Float.valueOf(cellValue.toString());
      } else if (clz == Date.class) {
        cellValue = DateUtil.getJavaDate((Double) cellValue);
      } else if (clz == BigDecimal.class) {
        cellValue = new BigDecimal(cellValue.toString());
      }
      return cellValue;
    } catch (NumberFormatException | IllegalStateException e) {
      log.warn("数据格式错误：第{}行，第{}列", (cell.getRowIndex() + 1), (cell.getColumnIndex() + 1), e);
      throw new MobeServiceException(String.format("数据格式错误：第%s行，第%s列",
          (cell.getRowIndex() + 1),
          (cell.getColumnIndex() + 1)));
    }
  }

  /**
   * 获取 excel 定义的结构信息.
   *
   * @param clz excel
   * @return
   */
  public static ExcelStructureInfo getExcelStructureInfo(Class<?> clz)
      throws IntrospectionException {
    ExcelStructureInfo structureInfo = new ExcelStructureInfo();
    // sheet 头信息
    List<ExcelItemAnnotationInfo<ExcelHeader>> headerList = ExcelUtil.getAnnotationInfo(clz,
        ExcelHeader.class);
    if (isNotEmpty(headerList)) {
      // 所有注解 header 的字段
      List<HeaderField> headerFields = newArrayListWithExpectedSize(headerList.size());
      for (ExcelItemAnnotationInfo<ExcelHeader> headerAnnotationInfo : headerList) {
        HeaderField headerField = new HeaderField();

        // 获取字段的注解信息
        ExcelHeader excelHeader = headerAnnotationInfo.getAnnotation();
        headerField.setIndex(excelHeader.index());
        // header 注解的类型里包含 column 注解的信息
        List<ExcelItemAnnotationInfo<ExcelColumn>> columnList = ExcelUtil.getAnnotationInfo(headerAnnotationInfo.getFieldType(),
            ExcelColumn.class);
        if (isNotEmpty(columnList)) {
          List<DataField> dataFields = newArrayListWithExpectedSize(columnList.size());
          for (ExcelItemAnnotationInfo<ExcelColumn> columnAnnotationInfo : columnList) {
            ExcelColumn excelColumn = columnAnnotationInfo.getAnnotation();
            DataField dataField = new DataField();
            dataField.setIndex(excelColumn.index());
            dataField.setName(excelColumn.name());
            dataField.setColspan(excelColumn.colspan());
            dataField.setRowspan(excelColumn.rowspan());
            dataField.setFontColor(excelColumn.fontColor());
            // 样式, 当前CellUtil有19种设置，期待 map 大小设置为20
            Map<String, Object> headerCellStyle = newHashMapWithExpectedSize(20);
            headerCommonCellStyle(headerCellStyle);
            headerCellStyle.put(CellUtil.FILL_FOREGROUND_COLOR, excelColumn.bgColor().getIndex());
            headerCellStyle.put(CellUtil.VERTICAL_ALIGNMENT, excelColumn.vertical());
            headerCellStyle.put(CellUtil.ALIGNMENT, excelColumn.horizontal());
            dataField.setHeaderCellStyle(headerCellStyle);

            dataFields.add(dataField);
          }
          headerField.setColumnFields(dataFields);
        }
        headerFields.add(headerField);
      }
      structureInfo.setHeaderFields(headerFields);
    }

    // 数据头信息
    List<ExcelItemAnnotationInfo<ExcelColumn>> columnList = ExcelUtil.getAnnotationInfo(clz,
        ExcelColumn.class);
    if (isNotEmpty(columnList)) {
      List<DataField> dataFields = newArrayListWithExpectedSize(columnList.size());
      for (ExcelItemAnnotationInfo<ExcelColumn> columnAnnotationInfo : columnList) {
        PropertyDescriptor propertyDescriptor
            = new PropertyDescriptor(columnAnnotationInfo.getFieldName(), clz);
        ExcelColumn excelColumn = columnAnnotationInfo.getAnnotation();
        DataField dataField = new DataField();
        dataField.setIndex(excelColumn.index());
        dataField.setWidth(excelColumn.width());
        dataField.setReadMethod(propertyDescriptor.getReadMethod());
        dataField.setName(excelColumn.name());
        dataField.setFontColor(excelColumn.fontColor());
        // 样式, 当前CellUtil有19种设置，期待 map 大小设置为20
        Map<String, Object> headerCellStyle = newHashMapWithExpectedSize(20);
        headerCommonCellStyle(headerCellStyle);
        headerCellStyle.put(CellUtil.ALIGNMENT, excelColumn.horizontal());
        headerCellStyle.put(CellUtil.VERTICAL_ALIGNMENT, excelColumn.vertical());
        headerCellStyle.put(CellUtil.FILL_FOREGROUND_COLOR, excelColumn.bgColor().getIndex());
        headerCellStyle.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);

        dataField.setHeaderCellStyle(headerCellStyle);

        Map<String, Object> dataCellStyle = newHashMapWithExpectedSize(20);
        dataCellStyle.put(CellUtil.ALIGNMENT, excelColumn.horizontal());
        dataCellStyle.put(CellUtil.VERTICAL_ALIGNMENT, excelColumn.vertical());
        dataCellStyle.put(CellUtil.FILL_FOREGROUND_COLOR, IndexedColors.WHITE.getIndex());
        dataCellStyle.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);
        dataCellStyle.put(CellUtil.WRAP_TEXT, true);
        commonCellStyle(dataCellStyle);
        dataField.setDataCellStyle(dataCellStyle);

        dataFields.add(dataField);
      }
      structureInfo.setDataFields(dataFields);
    }
    return structureInfo;
  }

  /**
   * 获取一个类的注解信息.
   *
   * @param clz 类型
   * @param annotation 注解信息
   * @return
   */
  public static <A extends Annotation> List<ExcelItemAnnotationInfo<A>> getAnnotationInfo(
      Class<?> clz, Class<A> annotation) {
    Field[] declaredFields = clz.getDeclaredFields();
    if (isEmpty(declaredFields)) {
      return EMPTY_LIST;
    }
    if (log.isDebugEnabled()) {
      log.debug("declaredFields - {}, length - {}", clz, declaredFields.length);
    }
    List<ExcelItemAnnotationInfo<A>> annotationFieldList = newArrayListWithExpectedSize(
        declaredFields.length);
    for (Field declaredField : declaredFields) {
      if (declaredField.isAnnotationPresent(annotation)) {
        ExcelItemAnnotationInfo<A> itemAnnotationInfo = new ExcelItemAnnotationInfo<>();
        // 字段名称
        itemAnnotationInfo.setFieldName(declaredField.getName());
        // 字段类型
        itemAnnotationInfo.setFieldType(declaredField.getType());
        // 注解信息
        itemAnnotationInfo.setAnnotation(declaredField.getAnnotation(annotation));
        annotationFieldList.add(itemAnnotationInfo);
      }
    }
    if (isEmpty(annotationFieldList)) {
      return EMPTY_LIST;
    }
    return annotationFieldList;
  }

  /**
   * 公共的标题样式.
   *
   * @param headerCellStyle 样式 map
   */
  private static void headerCommonCellStyle(Map<String, Object> headerCellStyle) {
    commonCellStyle(headerCellStyle);
    headerCellStyle.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);
    headerCellStyle.put(CellUtil.WRAP_TEXT, true);
  }

  /**
   * 公共样式.
   *
   * @param cellStyle 样式map
   */
  private static void commonCellStyle(Map<String, Object> cellStyle) {
    cellStyle.put(CellUtil.BORDER_LEFT, BorderStyle.THIN);
    cellStyle.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN);
    cellStyle.put(CellUtil.BORDER_TOP, BorderStyle.THIN);
    cellStyle.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN);

    cellStyle.put(CellUtil.LEFT_BORDER_COLOR, IndexedColors.BLACK.getIndex());
    cellStyle.put(CellUtil.RIGHT_BORDER_COLOR, IndexedColors.BLACK.getIndex());
    cellStyle.put(CellUtil.TOP_BORDER_COLOR, IndexedColors.BLACK.getIndex());
    cellStyle.put(CellUtil.BOTTOM_BORDER_COLOR, IndexedColors.BLACK.getIndex());
  }

  /**
   * 设置合并单元格的边框.
   *
   * @param sheet sheet
   * @param address 合并区域
   * @param borderStyle 边框样式
   * @param color 边框颜色
   */
  public static void setRegionBorder(
      Sheet sheet, CellRangeAddress address, BorderStyle borderStyle, short color) {
    int firstRow = address.getFirstRow();
    int lastRow = address.getLastRow();
    int firstColumn = address.getFirstColumn();
    int lastColumn = address.getLastColumn();

    for (int i = firstRow; i <= lastRow; i++) {
      Cell firstCell = CellUtil.getCell(CellUtil.getRow(i, sheet), firstColumn);
      Cell lastCell = CellUtil.getCell(CellUtil.getRow(i, sheet), lastColumn);

      CellUtil.setCellStyleProperty(firstCell, CellUtil.BORDER_LEFT, borderStyle);
      CellUtil.setCellStyleProperty(firstCell, CellUtil.LEFT_BORDER_COLOR, color);
      CellUtil.setCellStyleProperty(lastCell, CellUtil.BORDER_RIGHT, borderStyle);
      CellUtil.setCellStyleProperty(lastCell, CellUtil.RIGHT_BORDER_COLOR, color);
    }

    for (int i = firstColumn; i <= lastColumn; i++) {
      Cell firstCell = CellUtil.getCell(CellUtil.getRow(firstRow, sheet), i);
      Cell lastCell = CellUtil.getCell(CellUtil.getRow(lastRow, sheet), i);

      CellUtil.setCellStyleProperty(firstCell, CellUtil.BORDER_TOP, borderStyle);
      CellUtil.setCellStyleProperty(firstCell, CellUtil.TOP_BORDER_COLOR, color);
      CellUtil.setCellStyleProperty(lastCell, CellUtil.BORDER_BOTTOM, borderStyle);
      CellUtil.setCellStyleProperty(lastCell, CellUtil.BOTTOM_BORDER_COLOR, color);
    }
  }

}
