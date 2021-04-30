/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.extra.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 16/7/15.
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
  @Override
  public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getValueAsString();
    LocalTime localTime = LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm:ss"));
    return localTime;
  }
}
