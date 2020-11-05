package cloud.mobe.utils.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导入导出Excel文件定义.
 *
 * @author lsw1991abc@gmail.com
 * @date 2018/9/12 13:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ExcelFile {

  /**
   * 导出的文件名称.
   *
   * @return string
   */
  String value() default "";

  /**
   * sheet页名称.
   *
   * @return string
   */
  String sheet() default "";

  /**
   * 表格头行数.
   *
   * <p>用于导入，设定表格头所在的位置，以0开始
   *
   * @return int
   */
  int headerIndex() default 1;
}
