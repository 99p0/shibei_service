package cn.birdplanet.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: TestGuava
 * @date 2019/9/17 15:14
 */
@Slf4j
public class GuavaTest {

  @Test
  public void ac() {
    BigDecimal percent =
        new BigDecimal(80).divide(new BigDecimal(100));
    BigDecimal avgBonus = new BigDecimal("2000")
        .multiply(percent)
        .divide(new BigDecimal(String.valueOf(21)), 3, BigDecimal.ROUND_DOWN);
    log.debug("radio::{} avgBonus::{}  ", percent, avgBonus);
  }

  @Test
  public void testJoiner() {
    String uuid = Arrays.asList("C,F,D,F,,,,".split(","))
        .stream()
        .distinct()
        .filter(str -> StringUtils.isNotBlank(str))
        .map(str -> "'" + str + "'")
        .sorted()
        .collect(
            Collectors.joining(","));
    log.debug("uuid :: {}", uuid);
  }
}
