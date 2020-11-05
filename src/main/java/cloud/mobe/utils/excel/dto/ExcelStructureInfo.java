package cloud.mobe.utils.excel.dto;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel结构信息.
 *
 * @author lsw1991abc@gmail.com
 * @since 2020/4/13 22:27
 */
@Getter
@Setter
public class ExcelStructureInfo {

  private List<DataField> dataFields;
  private List<HeaderField> headerFields;

  @Setter
  @Getter
  public static class HeaderField {
    private int index;
    private List<DataField> columnFields;
  }

  @Getter
  @Setter
  public static class DataField {
    /** 列的字段标识. */
    private int index;
    /** 单元格宽度. */
    private int width;
    /** 数据类型. */
    private Class<?> type;
    /** 获取数据的方法. */
    private Method readMethod;
    /** 字段名. */
    private String name;
    /** 单元格内容格式. */
    private String dataFormat;
    /** 合并列数量. */
    private int colspan;
    /** 合并行数量. */
    private int rowspan;
    /** 水平位置. */
    private HorizontalAlignment horizontal;
    /** 垂直位置. */
    private VerticalAlignment vertical;
    /** 字体颜色. */
    private IndexedColors fontColor;
    /** 背景颜色. */
    private IndexedColors bgColor;
    /** 表头样式. */
    private Map<String, Object> headerCellStyle;
    /** 数据样式. */
    private Map<String, Object> dataCellStyle;
  }
}
