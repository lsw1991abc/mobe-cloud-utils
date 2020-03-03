package cloud.mobe.utils.datetime;

import java.time.format.DateTimeFormatter;
import lombok.Getter;

/**
 * 时间日期格式.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019-08-13 16:06
 */
public enum DateTimeFormat {

  JAVA_DEFAULT_FORMAT("EEE MMM dd HH:mm:ss zzz yyyy"),
  /** 短时间格式. */
  SHORT_CHINESE("yyyy年MM月dd日"),
  SHORT_NONE("yyyyMMdd"),
  SHORT_LINE("yyyy-MM-dd"),
  SHORT_LINE_YYYYMM("yyyy-MM"),
  SHORT_SLASH("yyyy/MM/dd"),
  SHORT_SLASH_YYYYMM("yyyy/MM"),
  SHORT_DOUBLE_SLASH("yyyy\\MM\\dd"),
  SHORT_NONE_YYMMDD("yyMMdd"),
  SHORT_NONE_YYYYMM("yyyyMM"),
  /** 长时间格式. */
  LONG_NONE("yyyyMMdd HH:mm:ss"),
  LONG_NONE_WITH_MILSEC("yyyyMMdd HH:mm:ss.SSS"),
  LONG_NONE_CROWD("yyyyMMddHHmmss"),
  LONG_LINE("yyyy-MM-dd HH:mm:ss"),
  LONG_LINE_WITH_MILSEC("yyyy-MM-dd HH:mm:ss.SSS"),
  LONG_SLASH("yyyy/MM/dd HH:mm:ss"),
  LONG_SLASH_WITH_MILSEC("yyyy/MM/dd HH:mm:ss.SSS"),
  LONG_LINE_YYYYMMDDHHMM("yyyy-MM-dd HH:mm"),
  LONG_LINE_YYYYMMDDHH("yyyy-MM-dd HH"),
  LONG_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss"),
  LONG_DOUBLE_SLASH_WITH_MILSEC("yyyy\\MM\\dd HH:mm:ss.SSS");

  @Getter
  private transient DateTimeFormatter formatter;

  DateTimeFormat(String pattern) {
    formatter = DateTimeFormatter.ofPattern(pattern);
  }
}
