/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.search;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: BinarySearchUtils
 * @date 2019-05-10 12:04
 */
@Slf4j
public final class BinarySearch {

  public static int binary(int[] arr, int data) {
    int count = 0;
    int min = 0;
    int max = arr.length - 1;
    int mid;
    while (min <= max) {
      //mid = (min + max) / 2;
      // 防止溢出
      //mid = min + (max - min) / 2;
      // 无符号位运算符的优先级较低，先括起来
      mid = min + ((max - min) >>> 1);
      System.out.printf("count >>> %s  mid >>> %s%n", ++count, mid);
      if (arr[mid] > data) {
        max = mid - 1;
      } else if (arr[mid] < data) {
        min = mid + 1;
      } else {
        return mid;
      }
    }
    return -1;
  }
  //
  //public static void main(String[] args) {
  //
  //  int[] arr = new int[10];
  //  arr[5] = 1;
  //  arr[2] = 22;
  //  System.out.println("快速排序前：" + Arrays.toString(arr));
  //  //arr = BubbleSort.sort(arr);
  //  //System.out.println("快速排序后：" + Arrays.toString(arr));
  //  System.out.println(binary(arr, 22));
  //}
}
