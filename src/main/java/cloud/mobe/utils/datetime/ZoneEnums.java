package cloud.mobe.utils.datetime;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * 时区工具类.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019-06-11 11:02
 */
public enum ZoneEnums {
  /** 中国. */
  CHINA(ZoneId.of("Asia/Shanghai"), ZoneOffset.ofHours(8), "中国");

  private ZoneId zoneId;
  private ZoneOffset zoneOffset;
  private String desc;

  ZoneEnums(ZoneId zoneId, ZoneOffset zoneOffset, String desc) {
    this.zoneId = zoneId;
    this.zoneOffset = zoneOffset;
    this.desc = desc;
  }

  public ZoneId getZoneId() {
    return zoneId;
  }

  public ZoneOffset getZoneOffset() {
    return zoneOffset;
  }

  public String getDesc() {
    return desc;
  }
}
