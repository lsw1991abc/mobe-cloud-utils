package cloud.mobe.utils.datetime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * 新旧时间类型转换工具类.
 *
 * @author lsw1991abc@gmail.com
 * @date 2018/9/4 17:44
 */
@Slf4j
public abstract class DateTimeConverter {
  /**
   * date与localDate转换.
   *
   * @param dateTime jdk8中的日期
   * @param zoneOffset zone offset
   * @return date
   */
  public static Date dateFromLocalDateTime(LocalDateTime dateTime, ZoneOffset zoneOffset) {
    Instant instant = dateTime.toInstant(zoneOffset);
    return Date.from(instant);
  }

  /**
   * date与localDate转换.
   *
   * @param dateTime jdk8中的日期
   * @return date
   */
  public static Date dateFromLocalDateTime(LocalDateTime dateTime) {
    Instant instant = dateTime.toInstant(ZoneEnums.CHINA.getZoneOffset());
    return Date.from(instant);
  }
}
