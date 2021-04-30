/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtils {
  //
  //public static void main(String[] args) {
  //  printDuplicateCharacters("Programming");
  //  printDuplicateCharacters("Combination");
  //  printDuplicateCharacters("Java");
  //}

  /**
   * 获取相同的字符
   *
   * @param word 原始字符
   */
  public static void printDuplicateCharacters(String word) {
    if (Strings.isNullOrEmpty(word)) {
      return;
    }
    char[] characters = word.toCharArray();
    // build HashMap with character and number of times they appear in String
    Map<Character, Integer> charMap = Maps.newHashMapWithExpectedSize(word.length() / 3);
    for (Character ch : characters) {
      if (charMap.containsKey(ch)) {
        charMap.put(ch, charMap.get(ch) + 1);
      } else {
        charMap.put(ch, 1);
      }
    }
    // Iterate through HashMap to print all duplicate characters of String
    Set<Map.Entry<Character, Integer>> entrySet = charMap.entrySet();
    System.out.printf("List of duplicate characters in String '%s' %n", word);
    for (Map.Entry<Character, Integer> entry : entrySet) {
      if (entry.getValue() > 1) {
        System.out.printf("%s : %d %n", entry.getKey(), entry.getValue());
      }
    }
  }
}
