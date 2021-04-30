/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.Admin;
import cn.birdplanet.daka.infrastructure.persistence.punch.AdminMapper;
import cn.birdplanet.daka.infrastructure.service.IAdminService;
import cn.birdplanet.toolkit.crypto.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AdminServiceImpl
 * @date 2019-08-13 19:27
 */
@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {

  @Autowired private AdminMapper adminMapper;

  @Override public Admin getByUserNameAndPwd(String username, String password) {
    Example example = new Example(Admin.class);
    example.createCriteria().andEqualTo("account", username)
        .andEqualTo("status", 1);
    Admin admin = adminMapper.selectOneByExample(example);
    try {
      boolean flag = BCrypt.checkpw(DigestUtils.sha512Hex(password), admin.getPassword());
      if (flag) {
        return admin;
      }
    } catch (Exception e) {
      log.error("BCrypt 校验密码 error", e);
      return null;
    }
    return null;
  }

  @Override public boolean changeAdminPasswordByUid(long uid, String password) {
    String pwdUseBcrypt = BCrypt.hashpw(DigestUtils.sha512Hex(password), BCrypt.gensalt());
    return adminMapper.changeAdminPasswordByUid(uid, pwdUseBcrypt) == 1;
  }
}
