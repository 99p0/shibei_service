package cn.birdplanet.commons.util;

import cn.birdplanet.toolkit.RedPackageForWx;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RedPackageForWxTest
 * @date 2020/1/11 05:33
 */
class RedPackageForWxTest {

  @Test
  void getRandomMoney() {
    for (int i = 0; i < 50; i++) {
      RedPackageForWx.RedPackage moneyPackage = new RedPackageForWx.RedPackage();
      moneyPackage.remainMoney = BigDecimal.valueOf(100);
      moneyPackage.remainSize = 5;

      while (moneyPackage.remainSize != 0) {
        System.out.print(RedPackageForWx.getRandomMoney(moneyPackage) + "   +");
      }

      System.out.println();
    }
  }
}
