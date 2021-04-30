/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SinglyLinkedTest {

  public static int[] stringToIntegerArray(String input) {
    input = input.trim();
    if (input.length() == 0) {
      return new int[0];
    }
    if (input.endsWith(",")) {
      input = input.substring(0, input.length() - 1);
    }

    String[] parts = input.split(",");
    int[] output = new int[parts.length];
    for (int index = 0; index < parts.length; index++) {
      String part = parts[index].trim();
      output[index] = Integer.parseInt(part);
    }
    return output;
  }

  public static ListNode stringToListNode(String input) {
    // Generate array from the input
    int[] nodeValues = stringToIntegerArray(input);

    // Now convert that list into linked list
    ListNode dummyRoot = new ListNode(0);
    ListNode ptr = dummyRoot;
    for (int item : nodeValues) {
      ptr.next = new ListNode(item);
      ptr = ptr.next;
    }
    return dummyRoot.next;
  }

  public static void prettyPrintLinkedList(ListNode node) {
    while (node != null && node.next != null) {
      System.out.print(node.val + "->");
      node = node.next;
    }

    if (node != null) {
      System.out.println(node.val);
    } else {
      System.out.println("Empty LinkedList");
    }
  }

  @Test
  public void test1() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = in.readLine()) != null) {
      SinglyLinkedTest.ListNode node = SinglyLinkedTest.stringToListNode(line);
      SinglyLinkedTest.prettyPrintLinkedList(node);
    }
  }

  // Definition for singly-linked list.
  public static class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
      val = x;
    }
  }
}

