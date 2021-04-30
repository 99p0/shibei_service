/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.util;

import cn.birdplanet.toolkit.core.lock.RwLocker;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LockTest {

  private static int count = 0;

  public static int getCount() {
    return count;
  }

  public static void addCount() {
    // 读写锁
    Lock writeLock = RwLocker.INSTANCE.writeLock();
    // 上锁
    writeLock.lock();
    count++;
    // 释放锁
    writeLock.unlock();
  }

  @Test
  public void test() {

    ExecutorService executorService = new ThreadPoolExecutor(10, 1000,
        60L, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10));

    for (int i = 0; i < 1000; i++) {
      Runnable r = () -> LockTest.addCount();
      executorService.execute(r);
    }
    executorService.shutdown();
    System.out.println(LockTest.getCount());
  }
}
