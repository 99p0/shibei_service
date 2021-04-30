package cn.birdplanet.foundation.util.date;

import cn.birdplanet.toolkit.date.DateUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: DateUtilTest
 * @date 2020/1/29 01:29
 */
class DateUtilTest {

  LocalDate startDate = LocalDate.of(2020, 1, 27);
  LocalDate endDate = LocalDate.of(2020, 1, 28);

  @Test
  void until() {
    System.out.println(DateUtil.until(startDate, endDate));
  }

  @Test
  void testUntil() {
    System.out.println(DateUtil.until(endDate));
  }
}
