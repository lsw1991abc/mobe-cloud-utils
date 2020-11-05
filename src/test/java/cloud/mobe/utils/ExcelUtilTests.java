package cloud.mobe.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cloud.mobe.utils.entity.json.JsonEntity;
import cloud.mobe.utils.json.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

/**
 * ExcelUtil 单元测试.
 *
 * @author lsw1991abc@gmail.com
 * @since 2020/6/2 10:18
 */
class ExcelUtilTests {

  @Test
  void testParseJsonString() {
    String jsonString = "{\"name\":\"Wizard\",\"city_name\":\"Qingdao\"}";

    JsonEntity entity1 = JsonUtil.parseJsonString(jsonString, JsonEntity.class);
    assertEquals("Wizard", entity1.getName());
    assertEquals("Qingdao", entity1.getCityName());

    JsonEntity entity2 = JsonUtil.parseJsonString(jsonString, new TypeReference<JsonEntity>() {});
    assertEquals("Wizard", entity2.getName());
    assertEquals("Qingdao", entity2.getCityName());
  }
}
