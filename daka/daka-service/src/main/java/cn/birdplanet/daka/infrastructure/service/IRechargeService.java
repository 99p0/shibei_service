package cn.birdplanet.daka.infrastructure.service;

import cn.birdplanet.daka.domain.po.Recharge;
import java.util.List;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IRechargeService
 * @date 2019/9/15 14:54
 */
public interface IRechargeService {

  boolean initRecharge(Recharge recharge);

  Recharge getByTradeNo(String tradeno);

  Recharge getByOrdersn(String ordersn);

  boolean updateTradeStatus(long uid, String ordersn, String tradeno, String tradeStatus);

  boolean updateByIdWithRechargeSucc(long id, String trade_no, String tradeStatus,
      String reqParams);

  List<Recharge> getByUidWithPage(int pageNum, int pageSize, long uid);

  List<Recharge> getAllWithPage(int pageNum, int pageSize);
}
