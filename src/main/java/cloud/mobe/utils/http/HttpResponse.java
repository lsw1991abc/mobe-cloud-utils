package cloud.mobe.utils.http;

import static cloud.mobe.utils.CheckEmptyUtil.isEmpty;
import static cloud.mobe.utils.CheckEmptyUtil.isNotEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * http 响应信息.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019/10/31 11:22
 */
@NoArgsConstructor
public class HttpResponse {

  @Getter private int code;
  private Headers headers;
  private Map<String, List<String>> multiHeaders;
  private String body;
  @Getter private String notes;

  /**
   * 默认构造.
   *
   * @param response response
   * @param notes notes
   * @throws IOException io exception
   */
  public HttpResponse(Response response, String notes) throws IOException {
    if (response != null) {
      this.code = response.code();
      this.headers = response.headers();
      multiHeaders = this.headers.toMultimap();
      ResponseBody body = response.body();
      this.body = (body == null) ? "" : body.string();
      this.notes = isEmpty(notes) ? "success" : notes;
    } else {
      this.code = -1;
      this.notes = isEmpty(notes) ? "response is null" : notes;
    }
  }

  /**
   * 获取头信息.
   *
   * @param key key
   * @return string
   */
  public String getHeader(String key) {
    return (isNotEmpty(key) && this.headers != null) ? this.headers.get(key) : "";
  }

  /**
   * 获取头信息.
   *
   * @param key key
   * @return string list
   */
  public List<String> getHeaders(String key) {
    return (isNotEmpty(key) && this.multiHeaders != null)
        ? this.multiHeaders.get(key)
        : emptyList();
  }

  /**
   * 获取头列表信息.
   *
   * @return string
   */
  public Set<String> getHeaderNames() {
    return (this.headers == null) ? emptySet() : this.headers.names();
  }

  /**
   * 获取消息体内容.
   *
   * @return body
   */
  public String getBody() {
    return (this.body == null) ? "" : this.body;
  }

  /**
   * 请求是否成功.
   *
   * @return boolean
   */
  public boolean isSuccessful() {
    return this.code >= 200 && this.code < 300;
  }
}
