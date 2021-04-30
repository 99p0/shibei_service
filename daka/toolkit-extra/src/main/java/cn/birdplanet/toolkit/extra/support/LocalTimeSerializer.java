/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.extra.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
  @Override
  public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    String formatStr = value.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    gen.writeString(formatStr);
  }
}
