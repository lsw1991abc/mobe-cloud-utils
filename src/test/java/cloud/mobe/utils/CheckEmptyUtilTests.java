package cloud.mobe.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * CheckEmptyUtil 单元测试.
 *
 * @author lsw1991abc@gmail.com
 */
class CheckEmptyUtilTests {

  @Test
  void testIsEmpty() {
    assertFalse(CheckEmptyUtil.isEmpty("abcStr"));
  }

  @Test
  void testIsOrEmpty() {
    assertTrue(CheckEmptyUtil.isOrEmpty());
    assertTrue(CheckEmptyUtil.isOrEmpty("abcStr", null));
    assertTrue(CheckEmptyUtil.isOrEmpty("   ", null));
    assertFalse(CheckEmptyUtil.isOrEmpty("abcStr", "defStr"));
  }

  @Test
  void testIsAndEmpty() {
    assertTrue(CheckEmptyUtil.isAndEmpty());
    assertFalse(CheckEmptyUtil.isAndEmpty("abcStr", null));
    assertTrue(CheckEmptyUtil.isAndEmpty("   ", null));
    assertFalse(CheckEmptyUtil.isAndEmpty("abcStr", "defStr"));
  }
}
