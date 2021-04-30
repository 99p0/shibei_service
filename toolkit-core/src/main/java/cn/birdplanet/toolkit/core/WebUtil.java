/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebUtil {

  public static String buildForm(String baseUrl, Map<String, String> parameters) {
    StringBuffer sb = new StringBuffer();
    sb.append("<form name=\"punchout_form\" method=\"post\" action=\"");
    sb.append(baseUrl);
    sb.append("\">\n");
    sb.append(buildHiddenFields(parameters));
    sb.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n");
    sb.append("</form>\n");
    sb.append("<script>document.forms[0].submit();</script>");
    String form = sb.toString();
    return form;
  }

  private static String buildHiddenFields(Map<String, String> parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      StringBuffer sb = new StringBuffer();
      Set<String> keys = parameters.keySet();
      Iterator var3 = keys.iterator();

      while (var3.hasNext()) {
        String key = (String) var3.next();
        String value = parameters.get(key);
        if (key != null && value != null) {
          sb.append(buildHiddenField(key, value));
        }
      }

      String result = sb.toString();
      return result;
    } else {
      return "";
    }
  }

  private static String buildHiddenField(String key, String value) {
    StringBuffer sb = new StringBuffer();
    sb.append("<input type=\"hidden\" name=\"");
    sb.append(key);
    sb.append("\" value=\"");
    String a = value.replace("\"", "&quot;");
    sb.append(a).append("\">\n");
    return sb.toString();
  }

  /**
   * 校验url是否有效的URL
   *
   * @param url 链接
   * @return 是否有效
   */
  public static boolean isWebUrl(String url) {
    if (!Strings.isNullOrEmpty(url)) {
      return url.startsWith("http://") || url.startsWith("https://");
    }
    return false;
  }
}
