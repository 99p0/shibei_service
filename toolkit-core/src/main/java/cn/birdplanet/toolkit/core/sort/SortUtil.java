/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.sort;

import java.util.Stack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SortUtil {

  //public static void main(String[] args) {
  //
  //  int[] unsorted = {34, 32, 43, 12, 11, 32, 22, 21, 32};
  //  System.out.println("Unsorted array : " + Arrays.toString(unsorted));
  //
  //  iterativeQsort(unsorted);
  //  System.out.println("Sorted array : " + Arrays.toString(unsorted));
  //}

  // 不用递归的迭代快速排序示例
  public static void iterativeQsort(int[] numbers) {
    // 堆
    Stack<Integer> stack = new Stack();
    stack.push(0);
    stack.push(numbers.length);

    while (!stack.isEmpty()) {
      int end = stack.pop();
      int start = stack.pop();
      if (end - start < 2) {
        continue;
      }
      int p = start + ((end - start) / 2);
      p = partition(numbers, p, start, end);

      stack.push(p + 1);
      stack.push(end);

      stack.push(start);
      stack.push(p);
    }
  }

  /*
   * Utility method to partition the array into smaller array, and
   * comparing numbers to rearrange them as per quicksort algorithm.
   */
  private static int partition(int[] input, int position, int start, int end) {
    int l = start;
    int h = end - 2;
    int piv = input[position];
    swap(input, position, end - 1);

    while (l < h) {
      if (input[l] < piv) {
        l++;
      } else if (input[h] >= piv) {
        h--;
      } else {
        swap(input, l, h);
      }
    }
    int idx = h;
    if (input[h] < piv) {
      idx++;
    }
    swap(input, end - 1, idx);
    return idx;
  }

  /**
   * Utility method to swap two numbers in given array
   *
   * @param arr - array on which swap will happen
   */
  private static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }
}
