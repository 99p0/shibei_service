/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import cn.birdplanet.daka.domain.po.GameModeBonusTiered;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class Base64Test {

  @Test
  public void testBase() {
    String msg = "Hello, Base64!";
    Base64.Encoder enc = Base64.getEncoder();
    String encStr = new String(enc.encode(msg.getBytes()), Charset.defaultCharset());
    log.debug("编码 >>> {}", encStr);
    Base64.Decoder dec = Base64.getDecoder();
    String decStr = new String(dec.decode(encStr), Charset.defaultCharset());
    log.debug("解码 >>> {}", decStr);
  }

  @Test
  public void aa() {

    List<GameModeBonusTiered> tieredBonusList = Arrays.asList(
        new GameModeBonusTiered(1, new BigDecimal("0.800")),
        new GameModeBonusTiered(2, new BigDecimal("0.800")),
        new GameModeBonusTiered(3, new BigDecimal("0.800")),
        new GameModeBonusTiered(4, new BigDecimal("0.800")),
        new GameModeBonusTiered(5, new BigDecimal("1.000")),
        new GameModeBonusTiered(6, new BigDecimal("1.000")),
        new GameModeBonusTiered(7, new BigDecimal("1.000")),
        new GameModeBonusTiered(8, new BigDecimal("1.000")),
        new GameModeBonusTiered(9, new BigDecimal("1.500")),
        new GameModeBonusTiered(10, new BigDecimal("1.500")),
        new GameModeBonusTiered(11, new BigDecimal("1.500")),
        new GameModeBonusTiered(12, new BigDecimal("1.500")));

    BigDecimal tieredBonusAmount = new BigDecimal("0.000");
    Map<Integer, BigDecimal> dataMap = Maps.newHashMapWithExpectedSize(tieredBonusList.size());
    for (GameModeBonusTiered bonusTiered : tieredBonusList) {
      dataMap.put(bonusTiered.getRound(), tieredBonusList.stream()
          .filter(gameModeBonusTiered -> gameModeBonusTiered.getRound() <= bonusTiered.getRound())
          .map(GameModeBonusTiered::getBonus)
          .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    log.debug("解码 >>> {}", dataMap);
  }
}
