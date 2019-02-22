package cloud.mobe.boot.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * MobeObjectMapper.
 *
 * @author 李世伟
 * @since 2019-02-21 10:42
 */
public class MobeObjectMapper extends ObjectMapper {

  public MobeObjectMapper() {
    super();
    this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
  }
}
