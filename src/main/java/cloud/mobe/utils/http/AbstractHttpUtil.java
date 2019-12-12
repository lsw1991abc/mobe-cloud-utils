package cloud.mobe.utils.http;

import static cloud.mobe.utils.CheckEmptyUtil.isEmpty;
import static cloud.mobe.utils.CheckEmptyUtil.isNotEmpty;
import static cloud.mobe.utils.CheckEmptyUtil.isOrEmpty;
import static cloud.mobe.utils.JsonUtil.parseJsonString;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.FORM_DATA;
import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static okhttp3.ConnectionSpec.CLEARTEXT;
import static okhttp3.ConnectionSpec.COMPATIBLE_TLS;
import static okhttp3.ConnectionSpec.MODERN_TLS;
import static okhttp3.ConnectionSpec.RESTRICTED_TLS;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Http工具抽象类.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019/10/31 14:13
 */
@Slf4j
public abstract class AbstractHttpUtil {
  /** 单例的OkHttpClient. */
  private static final OkHttpClient OK_HTTP_CLIENT;
  /** 证书管理器. */
  private static final X509TrustManager CERT_TRUST_MANAGER =
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          // do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          // do nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[] {};
        }
      };

  private static SSLSocketFactory sslSocketFactory;
  private static SSLContext sslContext;

  /** 初始化ok http client. */
  static {
    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new X509TrustManager[] {CERT_TRUST_MANAGER}, new SecureRandom());
      sslSocketFactory = sslContext.getSocketFactory();
    } catch (Exception e) {
      log.error("HttpUtil sslSocketFactory 初始化失败", e);
    }
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (isNotEmpty(sslSocketFactory)) {
      builder.sslSocketFactory(sslSocketFactory, CERT_TRUST_MANAGER);
    }
    List<Interceptor> interceptors = customInterceptors();
    if (isNotEmpty(interceptors)) {
      interceptors.forEach(builder::addInterceptor);
    }
    OK_HTTP_CLIENT =
        builder
            .connectionSpecs(Arrays.asList(MODERN_TLS, COMPATIBLE_TLS, CLEARTEXT, RESTRICTED_TLS))
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .callTimeout(50, TimeUnit.SECONDS)
            .build();
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param clz {@link Class}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T getForObject(String url, Class<T> clz) throws IOException {
    HttpResponse response = get(url, null);
    return parseJsonString(response.getBody(), clz);
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param clz {@link Class}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T getForObject(String url, Map<String, String> headers, Class<T> clz)
      throws IOException {
    HttpResponse response = get(url, headers);
    return parseJsonString(response.getBody(), clz);
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param tr {@link TypeReference}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T getForObject(String url, TypeReference<T> tr) throws IOException {
    HttpResponse response = get(url, null);
    return parseJsonString(response.getBody(), tr);
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param tr {@link TypeReference}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T getForObject(String url, Map<String, String> headers, TypeReference<T> tr)
      throws IOException {
    HttpResponse response = get(url, headers);
    return parseJsonString(response.getBody(), tr);
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @return string body of resp
   * @throws IOException io exception
   */
  public static String getForString(String url) throws IOException {
    HttpResponse response = get(url, null);
    return response.getBody();
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @return string body of resp
   * @throws IOException io exception
   */
  public static String getForString(String url, Map<String, String> headers) throws IOException {
    HttpResponse response = get(url, headers);
    return response.getBody();
  }

  /**
   * get 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @return HttpResponse
   * @throws IOException io exception
   */
  public static HttpResponse get(@Nonnull String url, Map<String, String> headers)
      throws IOException {
    if (isEmpty(url)) {
      return new HttpResponse(null, "the url is null");
    }
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.get().url(url);
    if (isNotEmpty(headers)) {
      requestBuilder.headers(Headers.of(headers));
    }
    Request request = requestBuilder.build();
    try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
      return new HttpResponse(response, null);
    }
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param body 请求体
   * @param clz {@link Class}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T postForObject(String url, Object body, Class<T> clz) throws IOException {
    HttpResponse response = post(url, null, body);
    return parseJsonString(response.getBody(), clz);
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求体
   * @param clz {@link Class}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T postForObject(
      String url, Map<String, String> headers, Object body, Class<T> clz) throws IOException {
    HttpResponse response = post(url, headers, body);
    return parseJsonString(response.getBody(), clz);
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param body 请求体
   * @param tr {@link TypeReference}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T postForObject(String url, Object body, TypeReference<T> tr)
      throws IOException {
    HttpResponse response = post(url, null, body);
    return parseJsonString(response.getBody(), tr);
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求体
   * @param tr {@link TypeReference}
   * @param <T> type of dto
   * @return dto
   * @throws IOException io exception
   */
  public static <T> T postForObject(
      String url, Map<String, String> headers, Object body, TypeReference<T> tr)
      throws IOException {
    HttpResponse response = post(url, headers, body);
    return parseJsonString(response.getBody(), tr);
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param body 请求体
   * @return string body of resp
   * @throws IOException io exception
   */
  public static String postForString(String url, Object body) throws IOException {
    HttpResponse response = post(url, null, body);
    return response.getBody();
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求体
   * @return string body of resp
   * @throws IOException io exception
   */
  public static String postForString(String url, Map<String, String> headers, Object body)
      throws IOException {
    HttpResponse response = post(url, headers, body);
    return response.getBody();
  }

  /**
   * post 请求.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求体
   * @return HttpResponse
   * @throws IOException io exception
   */
  public static HttpResponse post(
      @Nonnull String url, Map<String, String> headers, @Nonnull Object body) throws IOException {
    if (isEmpty(url)) {
      return new HttpResponse(null, "the url is null");
    }
    if (isEmpty(body)) {
      return new HttpResponse(null, "the request body is null");
    }
    // 请求头 content-type 的处理
    if (isEmpty(headers)) {
      headers = newHashMapWithExpectedSize(2);
    }
    String httpContentType = headers.get(CONTENT_TYPE);
    if (isEmpty(httpContentType) || "null".equalsIgnoreCase(httpContentType)) {
      httpContentType = JSON_UTF_8.toString();
    }
    // 使用spring的工具，判断content-type是否正确，也可以用于区分类型
    MediaType parseMediaType = MediaType.parse(httpContentType);

    Headers okHeaders = Headers.of(headers);
    // 响应信息

    String type = parseMediaType.type();
    String subtype = parseMediaType.subtype();
    if (JSON_UTF_8.type().equalsIgnoreCase(type)) {
      if (JSON_UTF_8.subtype().equals(subtype)) {
        return postJson(url, okHeaders, String.valueOf(body));
      } else if (FORM_DATA.subtype().equals(subtype)) {
        if (body instanceof Map) {
          return postForm(url, okHeaders, (Map<String, String>) body);
        } else {
          throw new RuntimeException("Form请求数据结构应为Map");
        }
      }
    }
    log.error("未支持的MediaType - {}", parseMediaType.toString());
    throw new RuntimeException(format("未支持的MediaType - %s", parseMediaType.toString()));
  }

  /**
   * 发送post请求，数据类型为json.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求数据
   * @return HttpResponse {@link HttpResponse}
   * @throws IOException io exception
   */
  private static HttpResponse postJson(
      @NonNull String url, @NonNull Headers headers, @NonNull String body) throws IOException {
    RequestBody requestBody = RequestBody.create(MediaType.get(JSON_UTF_8.toString()), body);
    Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
    try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
      return new HttpResponse(response, null);
    }
  }

  /**
   * 发送post请求，数据类型为key=value.
   *
   * @param url 请求地址
   * @param headers 请求头
   * @param body 请求数据 Map
   * @return HttpResponse {@link HttpResponse}
   * @throws IOException io exception
   */
  private static HttpResponse postForm(
      @NonNull String url, @NonNull Headers headers, @NonNull Map<String, String> body)
      throws IOException {
    FormBody.Builder formBuilder = new FormBody.Builder();
    body.forEach(formBuilder::add);
    RequestBody formBody = formBuilder.build();
    Request request = new Request.Builder().url(url).headers(headers).post(formBody).build();
    try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
      return new HttpResponse(response, null);
    }
  }

  /**
   * 生成basic认证信息.
   *
   * @param username 用户名
   * @param password 密码
   * @return basic auth. (e.g. {@code Basic XXX})
   */
  public static String genBasicAuth(String username, String password) {
    if (isOrEmpty(username, password)) {
      return null;
    }
    String encoded =
        Base64.getEncoder()
            .encodeToString(format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8));
    return format("Basic %s", encoded);
  }

  /**
   * 自定义的过滤器.
   *
   * <p>子类可以通过重写该方法,自定义请求过滤器
   *
   * <p>默认的{@link cloud.mobe.utils.MobeHttpUtil}没有实现任何过滤器
   *
   * @return 过滤器列表,默认空
   */
  protected static List<Interceptor> customInterceptors() {
    return emptyList();
  }
}
