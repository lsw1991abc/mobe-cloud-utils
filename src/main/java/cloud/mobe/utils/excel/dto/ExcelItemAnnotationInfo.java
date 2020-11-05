package cloud.mobe.utils.excel.dto;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.Setter;

/**
 * Excel单项注解信息.
 *
 * @author lsw1991abc@gmail.com
 * @since 2020/4/13 22:27
 */
@Setter
@Getter
public class ExcelItemAnnotationInfo<A extends Annotation> {
  /** 字段名. */
  private String fieldName;
  /** 字段类型. */
  private Class<?> fieldType;
  /** 注解的信息. */
  private A annotation;
}
