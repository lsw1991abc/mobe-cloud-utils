package cloud.mobe.boot.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 校验为空的工具.
 *
 * @author lsw1991abc@gmail.com
 * @since 2019-02-14 09:43
 */
public class CheckEmptyUtil {

  /**
   * 判断一个参数是否为空.
   *
   * @param object 待校验参数
   * @return 如果object为null，不进行类型判断，直接返回为true. <br>
   *     如果object不为null，则针对不同的class，分别进行判断.<br>
   *     String: trim后length为０的时候返回true.<br>
   *     List: 返回list.isEmpty().<br>
   *     Map: 返回map.isEmpty().<br>
   *     Array: 如果length为０，返回true.<br>
   *     Set: 返回set.isEmpty().<br>
   *     CharSequence: length为0或者toString后trim为空，则返回true.<br>
   */
  public static boolean isEmpty(Object object) {
    if (object == null) {
      return true;
    }

    if (object instanceof String) {
      String str = (String) object;
      if (str.trim().length() == 0) {
        return true;
      }
    }

    if (object instanceof CharSequence) {
      CharSequence cs = (CharSequence) object;
      if (cs.length() == 0) {
        return true;
      }

      if (cs.toString().trim().length() == 0) {
        return true;
      }
    }

    if (object instanceof List<?>) {
      List<?> list = (List<?>) object;
      return list.isEmpty();
    }

    if (object.getClass().isArray()) {
      Object[] array = (Object[]) object;
      if (array.length == 0) {
        return true;
      }
    }

    if (object instanceof Map<?, ?>) {
      Map<?, ?> map = (Map<?, ?>) object;
      return map.isEmpty();
    }

    if (object instanceof Set<?>) {
      Set<?> set = (Set<?>) object;
      return set.isEmpty();
    }

    return false;
  }

  /**
   * 判断对象是否非空，其实现为isEmpty取反.
   *
   * @param object 要判断的对象
   * @return 对象不为空返回true, 否则返回false
   */
  public static boolean isNotEmpty(Object object) {
    return !isEmpty(object);
  }

  /**
   * 如果传入的所有参数中任意一个为空，则返回true.
   *
   * @param objects 要判断的对象数组
   * @return 判断结果
   */
  public static boolean isOrEmpty(Object... objects) {
    if (objects == null || objects.length == 0) {
      return true;
    }

    for (Object object : objects) {
      if (isEmpty(object)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 如果传入的参数都为空，返回true. 否则，返回false.
   *
   * @param objects 要判断的数组
   * @return 判断结果
   */
  public static boolean isAndEmpty(Object... objects) {
    if (objects == null || objects.length == 0) {
      return true;
    }

    for (Object object : objects) {
      if (!isEmpty(object)) {
        return false;
      }
    }

    return true;
  }
}
