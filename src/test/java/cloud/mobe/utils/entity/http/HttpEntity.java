package cloud.mobe.utils.entity.http;

import lombok.Data;

/**
 * HttpBin Entity.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019/10/31 14:16
 */
public class HttpEntity {

  @Data
  public static class IpResult {
    private String origin;
  }

  @Data
  public static class JuHe {
    private String resultCode;
    private String reason;
  }

}
