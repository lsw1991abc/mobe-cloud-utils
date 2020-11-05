package cloud.mobe.utils.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel 的标题头.
 *
 * @author lsw1991abc@gmail.com
 * @since 2018-11-27 09:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelHeader {

  /**
   * 默认.
   *
   * @return
   */
  String value() default "";

  /**
   * 头的开始行数.
   *
   * @return
   */
  int index();
}
