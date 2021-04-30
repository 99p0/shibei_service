/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.sort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BubbleSort {

  /**
   * 把第一个元素与第二个元素比较，如果第一个比第二个大，则交换他们的位置。接着继续比较第二个与第三个元素，如果第二个比第三个大，则交换他们的位置....
   * 我们对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，这样一趟比较交换下来之后，排在最右的元素就会是最大的数。 除去最右的元素，我们对剩余的元素做同样的工作，如此重复下去，直到排序完成
   *
   * @param arr 待排序数组
   * @return 排序数组
   */
  public static int[] sort(int[] arr) {
    int len = arr.length;
    for (int i = 0; i < len; i++) {
      for (int j = 0; j < len - i - 1; j++) {
        if (arr[j + 1] < arr[j]) {
          swap(arr, j);
        }
      }
    }
    return arr;
  }

  /**
   * 假如从开始的第一对到结尾的最后一对，相邻的元素之间都没有发生交换的操作，这意味着右边的元素总是大于等于左边的元素，此时的数组已经是有序的了，我们无需再对剩余的元素重复比较下去了。
   *
   * @param arr 待排序数组
   * @return 排序数组
   */
  public static int[] optimizedSort(int[] arr) {

    int len = arr.length;
    for (int i = 0; i < len; i++) {
      boolean flag = true;
      for (int j = 0; j < len - i - 1; j++) {
        if (arr[j + 1] < arr[j]) {
          flag = false;
          swap(arr, j);
        }
        //一趟下来是否发生位置交换
        if (flag) {
          break;
        }
      }
    }
    return arr;
  }

  private static void swap(int[] arr, int j) {
    int temp = arr[j];
    arr[j] = arr[j + 1];
    arr[j + 1] = temp;
  }
}
