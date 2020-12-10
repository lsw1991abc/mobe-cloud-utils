package cloud.mobe.utils.excel;

import static cloud.mobe.utils.CheckEmptyUtil.isEmpty;
import static cloud.mobe.utils.CheckEmptyUtil.isNotEmpty;

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel信息.
 *
 * @author lsw1991abc@gmail.com
 * @date 2018/9/19 16:19
 */
@Getter
@Setter
@Slf4j
public class ExcelDetail<T extends AbstractExcelDefinition> {

  /** 表格空间. */
  private Workbook workbook;
  private FormulaEvaluator evaluator;
  /** 文件名称. */
  private String fileName;
  /** sheet页名称. */
  private String sheetName;
  /** 被注解的字段，导出字段. */
  private List<Object[]> annotationFields;
  /** 实体类信息. */
  private Class<T> definitionClass;
  /** 当前行数. */
  private Integer rowNum;

  /**
   * 获取数据.
   *
   * @return data
   */
  public List<T> getData()
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    return this.getData(0, false);
  }

  /**
   * 获取数据.
   *
   * @param sheetAt sheet的序列号 0开始
   * @param ignoreCellValueException 忽略单元格异常提示。忽略后，对应的字段为null
   * @return data
   * @see #getData()
   */
  public List<T> getData(int sheetAt, boolean ignoreCellValueException)
      throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Sheet sheet = workbook.getSheetAt(sheetAt);

    int lastRowNum = sheet.getLastRowNum();
    if (log.isDebugEnabled()) {
      log.debug("current num - {}, total row num - {}", rowNum, lastRowNum);
    }
    List<T> importData = Lists.newArrayListWithExpectedSize(lastRowNum);
    for (; rowNum <= lastRowNum; rowNum++) {
      Row row = sheet.getRow(rowNum);
      if (isEmpty(row)) {
        continue;
      }
      // 用于判断当前行是否为空行，如果是空行，不作为数据
      boolean isEmptyRow = true;
      T data = definitionClass.newInstance();
      for (Object[] annotationField : annotationFields) {
        Cell cell = row.getCell((Integer) annotationField[0]);
        if ((cell == null)
            || (cell.getCellType() == CellType.BLANK)
            || (cell.getCellType() == CellType._NONE)) {
          continue;
        }
        isEmptyRow = false;
        // 获取单元格值
        Object cellValue;
        try {
          cellValue = ExcelUtil.getCellValue(this.evaluator, cell, (Class<?>) annotationField[3]);
        } catch (Exception e) {
          // 如果忽略异常提示，则不设置dto数据
          if (ignoreCellValueException) {
            continue;
          }
          throw e;
        }
        if (log.isDebugEnabled()) {
          log.debug(
              "row - {}, cell - {}, cellType - {}, cellValue - {}",
              rowNum,
              cell.getColumnIndex(),
              cell.getCellType(),
              cellValue);
        }
        if (isNotEmpty(cellValue)) {
          // set 值
          ((Method) annotationField[1]).invoke(data, cellValue);
        }
      }
      if (!isEmptyRow) {
        importData.add(data);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("row - {} is empty", rowNum);
        }
      }
    }
    return importData;
  }

  /**
   * 设置打印区域.
   *
   * @param sheetName sheet name
   * @param startx 开始坐标
   * @param starty 开始坐标
   * @param endx 结束坐标
   * @param endy 结束坐标
   */
  public void setPrintArea(String sheetName, int startx, int starty, int endx, int endy) {
    if (this.workbook == null) {
      return;
    }
    setPrintArea(workbook.getSheetIndex(sheetName), startx, endx, starty, endy);
  }

  /**
   * 设置打印区域.
   *
   * @param sheetIndex sheet index
   * @param startx 开始坐标
   * @param starty 开始坐标
   * @param endx 结束坐标
   * @param endy 结束坐标
   */
  public void setPrintArea(int sheetIndex, int startx, int starty, int endx, int endy) {
    if (this.workbook == null) {
      return;
    }
    workbook.setPrintArea(sheetIndex, startx, endx, starty, endy);
  }

  /**
   * 获取workbook的输入流.
   *
   * @return 如果workbook不为空，返回workbook对应的字节输入流，否则返回null
   * @throws IOException io exception
   */
  public ByteArrayInputStream getWorkbookInputStream() throws IOException {
    if (this.workbook == null) {
      return null;
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    this.workbook.write(os);
    return new ByteArrayInputStream(os.toByteArray());
  }
}
