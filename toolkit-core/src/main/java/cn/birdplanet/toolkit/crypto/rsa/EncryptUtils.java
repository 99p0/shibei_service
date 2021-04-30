/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.crypto.rsa;

import cn.birdplanet.toolkit.crypto.Base64;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncryptUtils {

  /**
   * RSA加密方法
   *
   * @param data 待加密数据
   * @param publicKey 公钥字符串
   * @throws Exception
   */
  public static String encrypt(byte[] data, String publicKey)
      throws Exception {

    byte[] keyBytes = Base64.decode(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    Key publicK = keyFactory.generatePublic(x509KeySpec);
    // 对数据加密
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, publicK);
    int inputLen = data.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    for (int i = 0; inputLen - offSet > 0; offSet = i * 117) {
      byte[] cache;
      if (inputLen - offSet > 117) {
        cache = cipher.doFinal(data, offSet, 117);
      } else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet);
      }

      out.write(cache, 0, cache.length);
      ++i;
    }

    byte[] encryptedData = out.toByteArray();
    out.close();
    return Base64.encode(encryptedData);
  }

  /**
   * RSA解密方法
   *
   * @param encryptedString 待解密数据
   * @param privateKey 私钥字符串
   * @throws Exception
   */
  public static String decrypt(String encryptedString, String privateKey)
      throws Exception {
    byte[] encryptedData = Base64.decode(encryptedString);
    byte[] keyBytes = Base64.decode(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, privateK);
    int inputLen = encryptedData.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    for (int i = 0; inputLen - offSet > 0; offSet = i * 128) {
      byte[] cache;
      if (inputLen - offSet > 128) {
        cache = cipher.doFinal(encryptedData, offSet, 128);
      } else {
        cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      ++i;
    }
    byte[] decryptedData = out.toByteArray();
    out.close();
    return new String(decryptedData, StandardCharsets.UTF_8);
  }
}
