/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtil {

  private static ObjectMapper mapper;

  // 设置一些通用的属性
  static {
    mapper = new ObjectMapper();
    // 如果存在未知属性，则忽略不报错
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private JacksonUtil() {
  }

  public static String obj2Json(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error(String.format("obj2Json %s", obj != null ? obj.toString() : "null"), e);
      return "";
    }
  }

  public static <T> T toJavaObject(String jsonStr, Class<T> objClass) {
    try {
      return mapper.readValue(jsonStr, objClass);
    } catch (IOException e) {
      log.error(String.format("toJavaObject exception: \n %s\n %s", jsonStr, objClass), e);
      return null;
    }
  }

  public static <T> T json2Collection(String jsonStr, JavaType javaType) {
    try {
      return mapper.readValue(jsonStr, javaType);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static JavaType getCollectionType(Class<?> parametrized, Class<?>... parameterClasses) {
    return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
  }
}
