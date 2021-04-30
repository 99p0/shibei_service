/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author
 */
@Slf4j
public class SoutNTest {

  static int result = 0;

  @Test
  public void test1() throws InterruptedException {
    int N = 1;
    Thread[] threads = new Thread[N];
    final Semaphore[] syncObjects = new Semaphore[N];
    for (int i = 0; i < N; i++) {
      syncObjects[i] = new Semaphore(1);
      if (i != N - 1) {
        syncObjects[i].acquire();
      }
    }
    for (int i = 0; i < N; i++) {
      final Semaphore lastSemphore = i == 0 ? syncObjects[N - 1] : syncObjects[i - 1];
      final Semaphore curSemphore = syncObjects[i];
      final int index = i;
      threads[i] = new Thread(() -> {
        try {
          while (true) {
            lastSemphore.acquire();
            System.out.println("thread" + index + ": " + result++);
            if (result > 100) {
              System.exit(0);
            }
            curSemphore.release();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      threads[i].start();
    }
  }
}
