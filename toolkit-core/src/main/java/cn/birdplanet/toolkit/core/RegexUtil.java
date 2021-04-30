/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 正则校验
 * <p>
 * Created by dwy on 2016/11/4.
 */
@Slf4j
public class RegexUtil {

  /**
   * 手机号码校验 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 13+任意数 15+除4的任意数 18+除1和4的任意数 17+除9的任意数 14+5和7
   * 14开头的号码目前为上网卡专属号段,如联通的是145,移动的是147
   */
  private static final String REGEX_MOBILE = "^(((1[3|4|5|6|7|8|9][0-9]))[0-9]{8})$";
  private static final String REGEX_IP4 = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

  /**
   * 邮箱校验
   */
  private static final String REGEX_EMAIL =
      "\\w+((-w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]{2,}";

  /**
   * 校验emoji表情
   */
  private static final String REGEX_EMOJI =
      "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]";

  private RegexUtil() {
    throw new UnsupportedOperationException("can't instantiate...");
  }

  /**
   * 校验是否邮箱格式
   *
   * @param email 输入邮箱
   * @return true false
   */
  public static boolean isEmail(CharSequence email) {
    return isMatch(REGEX_EMAIL, email);
  }

  /**
   * 校验是否手机号码格式
   *
   * @param mobile_phone 输入手机号码
   * @return true false
   */
  public static boolean isMobile(CharSequence mobile_phone) {
    return isMatch(REGEX_MOBILE, mobile_phone);
  }

  /**
   * 校验是否IP4格式
   *
   * @param ip4 输入IP4地址
   * @return true false
   */
  public static boolean isIP4(CharSequence ip4) {
    return isMatch(REGEX_IP4, ip4);
  }

  /**
   * 校验是否emoji
   *
   * @param emoji Unicode格式的emoji表情
   * @return true false
   */
  public static boolean isEmoji(CharSequence emoji) {
    if (emoji != null && emoji.length() > 0) {
      Pattern emoji_pattern =
          Pattern.compile(REGEX_EMOJI, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
      return emoji_pattern.matcher(emoji).find();
    }
    return false;
  }

  /**
   * 判断是否匹配正则
   *
   * @param regex 正则表达式
   * @param input 要匹配的字符串
   * @return {@code true}: 匹配<br>{@code false}: 不匹配
   */
  public static boolean isMatch(String regex, CharSequence input) {
    return input != null && input.length() > 0 && Pattern.matches(regex, input);
  }

  /**
   * 获取正则匹配的部分
   *
   * @param regex 正则表达式
   * @param input 要匹配的字符串
   * @return 正则匹配的部分
   */
  public static List<String> getMatches(String regex, CharSequence input) {
    if (input == null) return null;
    List<String> matches = new ArrayList<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);
    while (matcher.find()) {
      matches.add(matcher.group());
    }
    return matches;
  }

  /**
   * 获取正则匹配分组
   *
   * @param input 要分组的字符串
   * @param regex 正则表达式
   * @return 正则匹配分组
   */
  public static String[] getSplits(String input, String regex) {
    if (input == null) return null;
    return input.split(regex);
  }

  /**
   * 替换正则匹配的第一部分
   *
   * @param input 要替换的字符串
   * @param regex 正则表达式
   * @param replacement 代替者
   * @return 替换正则匹配的第一部分
   */
  public static String getReplaceFirst(String input, String regex, String replacement) {
    if (input == null) return null;
    return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
  }

  /**
   * 替换所有正则匹配的部分
   *
   * @param input 要替换的字符串
   * @param regex 正则表达式
   * @param replacement 代替者
   * @return 替换所有正则匹配的部分
   */
  public static String getReplaceAll(String input, String regex, String replacement) {
    if (input == null) return null;
    return Pattern.compile(regex).matcher(input).replaceAll(replacement);
  }
}
