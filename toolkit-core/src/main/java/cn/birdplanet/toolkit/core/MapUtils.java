/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapUtils {

  public static Map<String, String> conver(Map<String, String[]> paramMap) {
    Map<String, String> params = new HashMap<>();
    for (Iterator<String> iter = paramMap.keySet().iterator(); iter.hasNext(); ) {
      String name = iter.next();
      String[] values = paramMap.get(name);
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        valueStr = (i == values.length - 1) ? valueStr + values[i]
            : valueStr + values[i] + ",";
      }
      //乱码解决，这段代码在出现乱码时使用
      //try {
      //  valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
      //} catch (UnsupportedEncodingException e) {
      //  e.printStackTrace();
      //}
      params.put(name, valueStr);
    }
    return params;
  }
}
