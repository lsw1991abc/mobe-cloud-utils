package cloud.mobe.utils;

import cloud.mobe.utils.json.MobeObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.lang.reflect.Type;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Json工具类.
 *
 * <p>用于json和pojo之间的换换
 *
 * @author lsw1991abc@gmail.com
 * @since 2019-02-21 10:33
 */
@Slf4j
@UtilityClass
public class JsonUtil {

  /**
   * 将一个对象转换为json字符串.
   *
   * @param value 要转换的对象
   * @return 转换失败返回null, 否则返回json字符串
   */
  public static String getJsonString(Object value) {
    if (CheckEmptyUtil.isEmpty(value)) {
      return null;
    }
    MobeObjectMapper mapper = new MobeObjectMapper();
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      log.error("error when getJsonString!", ex);
      return null;
    }
  }

  /**
   * 将一个json字符串转换为对象.
   *
   * @param json 要转换的json字符串
   * @param clz 转换的对象类型
   * @return 转换失败返回null, 否则返回对象
   * @see #parseJsonString(String, TypeReference)
   */
  public static <T> T parseJsonString(String json, final Class<T> clz) {
    return parseJsonString(
        json,
        CheckEmptyUtil.isEmpty(clz)
            ? null
            : new TypeReference<T>() {
              @Override
              public Type getType() {
                return clz;
              }
            });
  }

  /**
   * 将一个json字符串转换为对象.
   *
   * @param json 要转换的json字符串
   * @param ref 转换的对象类型 {@code new TypeReference<String>() {}}
   * @return 转换失败返回null, 否则返回对象
   * @see #parseJsonString(String, Class)
   */
  public static <T> T parseJsonString(String json, TypeReference<T> ref) {
    if (CheckEmptyUtil.isOrEmpty(json, ref)) {
      return null;
    }
    MobeObjectMapper mapper = new MobeObjectMapper();
    try {
      return mapper.readValue(json, ref);
    } catch (IOException ex) {
      log.error("error when parseJsonString!", ex);
      return null;
    }
  }
}
