/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用 枚举 创建一个读写锁的单例
 */
@Slf4j
public enum RwLocker {

  INSTANCE;

  private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

  public Lock writeLock() {
    return READ_WRITE_LOCK.writeLock();
  }
}
