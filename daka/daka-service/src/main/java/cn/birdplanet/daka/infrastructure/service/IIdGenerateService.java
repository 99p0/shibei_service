/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.toolkit.extra.code.IdGenerateCodes;
import java.util.Map;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IidGenderService
 * @date 2019-05-08 19:26
 */
public interface IIdGenerateService {

  /**
   * 获取ordersn
   *
   * @return 一个新的ordersn
   */
  long ordersn();

  /**
   * 获取uid
   *
   * @return 一个新的uid
   */
  long nextUid();

  /**
   * 获取id
   *
   * @return 一个新的id
   */
  long nextId(IdGenerateCodes uidCode);

  long nextId();

  /**
   * 解析 uid
   *
   * @param uid id
   * @return uid 的意思
   */
  Map<String, Object> parse(long uid);
}
