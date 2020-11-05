package cloud.mobe.utils.exception;

/**
 * 统一异常.
 *
 * @author lsw1991abc@gmail.com
 * @since 2020/4/13 22:24
 */
public class MobeServiceException extends RuntimeException {
  public MobeServiceException(String msg) {
    super(msg);
  }

  public MobeServiceException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
