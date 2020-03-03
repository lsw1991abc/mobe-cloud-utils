package cloud.mobe.utils.datetime;

import static cloud.mobe.utils.datetime.DateTimeFormat.JAVA_DEFAULT_FORMAT;
import static cloud.mobe.utils.datetime.DateTimeFormat.LONG_LINE;
import static cloud.mobe.utils.datetime.DateTimeFormat.LONG_LINE_WITH_MILSEC;
import static cloud.mobe.utils.datetime.DateTimeFormat.LONG_SLASH;
import static cloud.mobe.utils.datetime.DateTimeFormat.LONG_SLASH_WITH_MILSEC;
import static cloud.mobe.utils.datetime.DateTimeFormat.SHORT_LINE;
import static cloud.mobe.utils.datetime.DateTimeFormat.SHORT_SLASH;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 时间工具类.
 *
 * @author lsw1991abc@gmail.com
 * @date 2018/9/4 17:44
 */
@Slf4j
public abstract class DateTimeUtil {
  private static final Pattern UNIX_TIMESTAMP_PATTERN = Pattern.compile("^\\d{10}$");
  private static final Pattern JAVA_TIMESTAMP_PATTERN = Pattern.compile("^\\d{13}$");
  private static final Pattern STANDARD_DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
  private static final Pattern SLASH_DATE_PATTERN = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$");
  /** 默认的时间格式. */
  private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = LONG_LINE.getFormatter();

  /**
   * String 转日期时间.
   *
   * @param dateTimeStr 参数
   * @return
   */
  public static LocalDateTime parseDateTime(final String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.trim().length() <= 0) {
      return null;
    }
    String newText = dateTimeStr.trim();
    int length = newText.length();
    switch (length) {
      case 10:
        return parse10Text(newText);
      case 13:
        return parse13Text(newText);
      case 19:
        return parse19Text(newText);
      case 23:
        return parse23Text(newText);
      default:
        return LocalDateTime.parse(dateTimeStr, JAVA_DEFAULT_FORMAT.getFormatter());
    }
  }

  /**
   * String 转日期时间.
   *
   * @param dateTimeStr 参数
   * @param format 时间格式
   * @return
   */
  public static LocalDateTime parseDateTime(final String dateTimeStr, DateTimeFormat format) {
    return LocalDateTime.parse(dateTimeStr, format.getFormatter());
  }

  /**
   * String 转日期时间.
   *
   * @param dateTimeStr 参数
   * @param format 时间格式
   * @return
   */
  public static LocalDateTime parseDateTime(final String dateTimeStr, DateTimeFormatter format) {
    return LocalDateTime.parse(dateTimeStr, format);
  }

  /**
   * 时间转 String.
   *
   * @param time 参数
   * @return
   */
  public static String parseDateTime(LocalDateTime time) {
    return DEFAULT_DATETIME_FORMATTER.format(time);
  }

  /**
   * 时间转 String.
   *
   * @param time 参数
   * @param format 时间格式
   * @return
   */
  public static String parseDateTime(LocalDateTime time, DateTimeFormat format) {
    return format.getFormatter().format(time);
  }

  /**
   * 时间转 String.
   *
   * @param time 参数
   * @param format 时间格式
   * @return
   */
  public static String parseDateTime(LocalDateTime time, DateTimeFormatter format) {
    return format.format(time);
  }

  public static LocalDate parseDate(String timeStr, DateTimeFormat format) {
    return LocalDate.parse(timeStr, format.getFormatter());
  }

  public static LocalDate parseDate(String timeStr, DateTimeFormatter format) {
    return LocalDate.parse(timeStr, format);
  }

  public static String parseDate(LocalDate time, DateTimeFormat format) {
    return format.getFormatter().format(time);
  }

  public static String parseDate(LocalDate time, DateTimeFormatter format) {
    return format.format(time);
  }

  /**
   * 获取当前时间.
   *
   * @return
   */
  public static String currentDatetime() {
    return DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
  }

  /**
   * 获取当前时间.
   *
   * @param format 时间格式
   * @return
   */
  public static String currentDatetime(DateTimeFormat format) {
    return format.getFormatter().format(LocalDateTime.now());
  }

  /**
   * 获取当前时间.
   *
   * @param format 时间格式
   * @return
   */
  public static String currentDatetime(DateTimeFormatter format) {
    return format.format(LocalDateTime.now());
  }

  /**
   * 创建一个新的日期，它的值为上月的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfLastMonth(LocalDate date) {
    date = date.plus(-1, MONTHS);
    return date.with((temporal) -> temporal.with(DAY_OF_MONTH, 1));
  }

  /**
   * 创建一个新的日期，它的值为上月的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfLastMonth(LocalDate date) {
    date = date.plus(-1, MONTHS);
    return date.with(
        (temporal) -> temporal.with(DAY_OF_MONTH, temporal.range(DAY_OF_MONTH).getMaximum()));
  }

  /**
   * 创建一个新的日期，它的值为当月的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfMonth(LocalDate date) {
    return date.with(TemporalAdjusters.firstDayOfMonth());
  }

  /**
   * 创建一个新的日期，它的值为当月的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfMonth(LocalDate date) {
    return date.with(TemporalAdjusters.lastDayOfMonth());
  }

  /**
   * 创建一个新的日期，它的值为下月的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfNextMonth(LocalDate date) {
    return date.with(TemporalAdjusters.firstDayOfNextMonth());
  }

  /**
   * 创建一个新的日期，它的值为下月的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfNextMonth(LocalDate date) {
    date = date.plus(1, MONTHS);
    return date.with(
        (temporal) -> temporal.with(DAY_OF_MONTH, temporal.range(DAY_OF_MONTH).getMaximum()));
  }

  /**
   * 创建一个新的日期，它的值为上年的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfLastYear(LocalDate date) {
    date = date.plus(-1, YEARS);
    return date.with((temporal) -> temporal.with(DAY_OF_YEAR, 1));
  }

  /**
   * 创建一个新的日期，它的值为上年的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfLastYear(LocalDate date) {
    date = date.plus(-1, YEARS);
    return date.with(
        (temporal) -> temporal.with(DAY_OF_YEAR, temporal.range(DAY_OF_YEAR).getMaximum()));
  }

  /**
   * 创建一个新的日期，它的值为当年的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfYear(LocalDate date) {
    return date.with(TemporalAdjusters.firstDayOfYear());
  }

  /**
   * 创建一个新的日期，它的值为今年的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfYear(LocalDate date) {
    return date.with(TemporalAdjusters.lastDayOfYear());
  }

  /**
   * 创建一个新的日期，它的值为明年的第一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstDayOfNextYear(LocalDate date) {
    return date.with(TemporalAdjusters.firstDayOfNextYear());
  }

  /**
   * 创建一个新的日期，它的值为明年的最后一天.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate lastDayOfNextYear(LocalDate date) {
    date = date.plus(1, YEARS);
    return date.with(
        (temporal) -> temporal.with(DAY_OF_YEAR, temporal.range(DAY_OF_YEAR).getMaximum()));
  }

  /**
   * 创建一个新的日期，它的值为同一个月中，第一个符合星期几要求的值.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate firstInMonth(LocalDate date, DayOfWeek dayOfWeek) {
    return date.with(TemporalAdjusters.firstInMonth(dayOfWeek));
  }

  /**
   * 创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星 期几要求的日期.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate next(LocalDate date, DayOfWeek dayOfWeek) {
    return date.with(TemporalAdjusters.next(dayOfWeek));
  }

  /**
   * 创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星 期几要求的日期.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate previous(LocalDate date, DayOfWeek dayOfWeek) {
    return date.with(TemporalAdjusters.previous(dayOfWeek));
  }

  /**
   * 创建一个新的日期，并将其值设定为日期调整后或者调整前， 第一个符合指定星 期几要求的日期，如果该日期已经符合要求，直接返回该对象.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate nextOrSame(LocalDate date, DayOfWeek dayOfWeek) {
    return date.with(TemporalAdjusters.nextOrSame(dayOfWeek));
  }

  /**
   * 创建一个新的日期，并将其值设定为日期调整后或者调整前， 第一个符合指定星 期几要求的日期，如果该日期已经符合要求，直接返回该对象.
   *
   * @param date 参数
   * @return
   */
  public static LocalDate previousOrSame(LocalDate date, DayOfWeek dayOfWeek) {
    return date.with(TemporalAdjusters.previousOrSame(dayOfWeek));
  }

  private static LocalDateTime parse10Text(String newText) {
    Matcher matcher = UNIX_TIMESTAMP_PATTERN.matcher(newText);
    Matcher standardMatcher = STANDARD_DATE_PATTERN.matcher(newText);
    Matcher slashMatcher = SLASH_DATE_PATTERN.matcher(newText);
    if (matcher.matches()) {
      return LocalDateTime.ofEpochSecond(Long.parseLong(newText), 0, ZoneOffset.ofHours(8));
    } else if (standardMatcher.matches()) {
      return LocalDateTime.parse(newText, SHORT_LINE.getFormatter());
    } else if (slashMatcher.matches()) {
      return LocalDateTime.parse(newText, SHORT_SLASH.getFormatter());
    } else {
      throw new IllegalArgumentException("wrong 10 length date format:" + newText);
    }
  }

  private static LocalDateTime parse13Text(String newText) {
    Matcher matcher = JAVA_TIMESTAMP_PATTERN.matcher(newText);
    if (matcher.matches()) {
      long timestamp = Long.parseLong(newText);
      return LocalDateTime.ofEpochSecond(
          timestamp / 1000, new Integer((timestamp % 1000) + ""), ZoneOffset.ofHours(8));
    } else {
      throw new IllegalArgumentException("wrong java timestamp format:" + newText);
    }
  }

  private static LocalDateTime parse19Text(String newText) {
    if (newText.contains("-")) {
      return LocalDateTime.parse(newText, DEFAULT_DATETIME_FORMATTER);
    } else {
      return LocalDateTime.parse(newText, LONG_SLASH.getFormatter());
    }
  }

  private static LocalDateTime parse23Text(String newText) {
    if (newText.contains("-")) {
      return LocalDateTime.parse(newText, LONG_LINE_WITH_MILSEC.getFormatter());
    } else {
      return LocalDateTime.parse(newText, LONG_SLASH_WITH_MILSEC.getFormatter());
    }
  }
}
