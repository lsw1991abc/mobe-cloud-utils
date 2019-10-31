package cloud.mobe.utils;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cloud.mobe.utils.entity.http.HttpEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * MobeHttpUtil 单元测试.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019/10/31 14:14
 */
public class MobeHttpUtilTests {

  @Test
  public void getForStringTest() throws IOException {
    String forString = MobeHttpUtil.getForString("http://httpbin.org/ip");
    System.out.println(forString);
    assertNotNull(forString);
  }

  @Test
  public void getForStringHttpsTest() throws IOException {
    String forString =
        MobeHttpUtil.getForString(
            "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=13333232232");
    System.out.println(forString);
    assertNotNull(forString);
  }

  @Test
  public void getForObjectTest() throws IOException {
    Map<String, String> httpHeaders = Maps.newHashMapWithExpectedSize(5);
    httpHeaders.put("key", "value");
    HttpEntity.IpResult result =
        MobeHttpUtil.getForObject("http://httpbin.org/ip", httpHeaders, HttpEntity.IpResult.class);
    assertNotNull(result);
    assertNotNull(result.getOrigin());
  }

  @Test
  public void postForStringTest() throws IOException {
    Map<String, String> httpHeaders = Maps.newHashMapWithExpectedSize(5);
    httpHeaders.put(CONTENT_TYPE, FORM_DATA.toString());
    Map<String, String> body = Maps.newHashMapWithExpectedSize(4);
    body.put("idcard", "mobe");
    body.put("realname", "mobe");
    body.put("mobile", "mobe");
    body.put("key", "mobe");

    String response =
        MobeHttpUtil.postForString(
            "http://apis.juhe.cn/telecomCmcc_sha256/query", httpHeaders, body);
    System.out.println(response);
    assertNotNull(response);
  }

  @Test
  public void postForObjectTest() throws IOException {
    HttpEntity.JuHe response =
        MobeHttpUtil.postForObject(
            "http://apis.juhe.cn/telecomCmcc_sha256/query",
            "{}",
            new TypeReference<HttpEntity.JuHe>() {});
    System.out.println(response);
    assertEquals("101", response.getResultCode());
  }
}
