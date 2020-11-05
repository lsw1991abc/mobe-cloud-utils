package cloud.mobe.utils.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel文件定义.
 *
 * @author lsw1991abc@gmail.com
 * @date 2018/9/12 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelColumn {
  /**
   * 字段名.
   *
   * @return
   */
  String name() default "";

  /**
   * 单元格宽度.
   *
   * @return
   */
  int width() default 6;

  /**
   * 字体颜色.
   *
   * @return
   */
  IndexedColors fontColor() default IndexedColors.WHITE;

  /**
   * 背景颜色.
   *
   * @return
   */
  IndexedColors bgColor() default IndexedColors.ROYAL_BLUE;

  /**
   * 水平位置.
   *
   * @return
   */
  HorizontalAlignment horizontal() default HorizontalAlignment.CENTER;

  /**
   * 垂直位置.
   *
   * @return
   */
  VerticalAlignment vertical() default VerticalAlignment.CENTER;

  /**
   * 列的字段标识 0,1,2,3.
   *
   * @return
   */
  int index() default 0;

  /**
   * 合并列数量.
   *
   * @return
   */
  int colspan() default 1;

  /**
   * 合并行数量.
   *
   * @return
   */
  int rowspan() default 1;
}
