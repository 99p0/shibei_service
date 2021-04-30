/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.schedulerx.service;

import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;

public interface IInviteService {

  boolean inputInviterCode(UserDtlVO userDtlVO, String code, User inviter);
}
