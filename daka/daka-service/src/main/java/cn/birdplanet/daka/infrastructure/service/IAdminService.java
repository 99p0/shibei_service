/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.Admin;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IAdminService
 * @date 2019-08-13 19:27
 */
public interface IAdminService {

  Admin getByUserNameAndPwd(String username, String password);

  boolean changeAdminPasswordByUid(long uid, String password);
}
