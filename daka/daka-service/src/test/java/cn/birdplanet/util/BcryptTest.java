/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import cn.birdplanet.toolkit.crypto.BCrypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

public class BcryptTest {

  @Test
  public void testBcrypt() {

    String originalPassword = "bp@190916";
    System.out.println("pw: \t\t\t\t" + originalPassword);

    String originalPassword_sha512 = DigestUtils.sha512Hex(originalPassword);
    System.out.println("pw_sha512: \t" + originalPassword_sha512);

    String salt = BCrypt.gensalt();
    System.out.println("salt:\t\t\t\t" + salt);

    String generatedSecuredPasswordHash = BCrypt.hashpw(originalPassword_sha512, salt);
    System.out.println("BCrypt:\t\t\t" + generatedSecuredPasswordHash);

    boolean matched = BCrypt.checkpw(originalPassword_sha512, generatedSecuredPasswordHash);
    System.out.println("Check:\t\t\t" + matched);
  }
}
