/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;

@Slf4j
public class DigestTest {

  @Test
  public void test() {

    String msg = "156北京海绵保科技有限公司01140019000143157610RMB96.0050114001900309656272OPENAPI";
    System.out.println("sha256Hex: " + DigestUtils.sha256Hex(msg));
    System.out.println("hmacSha256Hex: " + HmacUtils.hmacSha256Hex(msg, null));
    System.out.println("md5Hex: " + DigestUtils.md5Hex(msg));
    System.out.println("sha256Hex: " + DigestUtils.sha256Hex(DigestUtils.md5Hex(msg)));
  }
}
