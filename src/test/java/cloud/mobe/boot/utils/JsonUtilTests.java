package cloud.mobe.boot.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import cloud.mobe.boot.utils.entity.json.JsonEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

/**
 * TODO lishiwei desc.
 *
 * @author 李世伟
 * @since 2019-04-19 17:16
 */
class JsonUtilTests {

  @Test
  void testGetJsonStringIsNull() {
    String jsonString;

    jsonString = JsonUtil.getJsonString(null);
    assertNull(jsonString);

    JsonEntity entity = new JsonEntity();
    entity.setName("Wizard");
    entity.setCityName("Qingdao");

    jsonString = JsonUtil.getJsonString(entity);
    assertEquals("{\"name\":\"Wizard\",\"city_name\":\"Qingdao\"}", jsonString);
  }

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
