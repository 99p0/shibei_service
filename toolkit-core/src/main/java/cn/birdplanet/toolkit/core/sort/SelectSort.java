/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.sort;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: SelectSort
 * @description: 选择排序
 * @date 2019-05-10 12:31
 */
@Slf4j
public class SelectSort {

  /**
   * 首先，找到数组中最小的那个元素， 其次，将它和数组的第一个元素交换位置(如果第一个元素就是最小元素那么它就和自己交换)。 然后，在剩下的元素中找到最小的元素，将它与数组的第二个元素交换位置。
   * 如此往复，直到将整个数组排序。这种方法我们称之为选择排序
   *
   * @param arr 数组
   * @return 排序后的数组
   */
  public static int[] sort(int[] arr) {
    int len = arr.length;
    for (int i = 0, size = len - 1; i < size; i++) {
      int min = i;
      for (int j = i + 1; j < len; j++) {
        if (arr[min] > arr[j]) {
          min = j;
        }
      }
      swap(arr, min, i);
    }
    return arr;
  }

  private static void swap(int[] arr, int min, int i) {
    int temp = arr[min];
    arr[min] = arr[i];
    arr[i] = temp;
  }
}
