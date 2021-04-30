/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.Invite;
import cn.birdplanet.daka.domain.po.ServiceFuwu;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.toolkit.extra.code.FuwuTypeCodes;
import java.util.List;

public interface IInviteService {

  boolean inputInviterCode(UserDtlVO userDtlVO, String code, User inviter);

  boolean sendInvitedMessage(UserDtlVO currUserDtlVo, List<String> aliUids);

  List<Invite> getAllWithPage(int pageNum, int pageSize);

  List<Invite> getByUidWithPage(int pageNum, int pageSize, long uid);

  ServiceFuwu getFuwuByType(FuwuTypeCodes fuwuTypeCodes);
}
