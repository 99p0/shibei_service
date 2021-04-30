package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.domain.po.Recharge;
import cn.birdplanet.daka.infrastructure.persistence.punch.RechargeMapper;
import cn.birdplanet.daka.infrastructure.service.IRechargeService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: RechargeServiceImpl
 * @date 2019/9/15 14:54
 */
@Service
public class RechargeServiceImpl extends BaseService implements IRechargeService {

  @Autowired private RechargeMapper rechargeMapper;

  @Override public boolean initRecharge(Recharge recharge) {
    return rechargeMapper.insertSelective(recharge) == 1;
  }

  @Override public Recharge getByTradeNo(String tradeno) {
    if (StringUtils.isBlank(tradeno)) return null;
    Example example = new Example(Recharge.class);
    example.createCriteria().andEqualTo("tradeNo", tradeno);
    return rechargeMapper.selectOneByExample(example);
  }

  @Override public Recharge getByOrdersn(String ordersn) {
    if (StringUtils.isBlank(ordersn)) return null;
    Example example = new Example(Recharge.class);
    example.createCriteria().andEqualTo("ordersn", ordersn);
    return rechargeMapper.selectOneByExample(example);
  }

  @Override public boolean updateTradeStatus(long uid, String ordersn, String tradeno,
      String tradeStatus) {
    return rechargeMapper.updateTradeStatus(uid, ordersn, tradeno, tradeStatus) == 1;
  }

  @Override public boolean updateByIdWithRechargeSucc(long id, String trade_no,
      String tradeStatus, String reqParams) {
    return rechargeMapper.updateByIdWithRechargeSucc(id, trade_no, tradeStatus, reqParams) == 1;
  }

  @Override public List<Recharge> getByUidWithPage(int pageNum, int pageSize, long uid) {
    Example example = new Example(Recharge.class);
    Example.Criteria criteria = example.createCriteria();
    if (uid != 0L) {
      criteria.andEqualTo("uid", uid);
    }
    example.orderBy("id").desc();
    PageHelper.startPage(pageNum, pageSize);
    return rechargeMapper.selectByExample(example);
  }

  @Override public List<Recharge> getAllWithPage(int pageNum, int pageSize) {
    return this.getByUidWithPage(pageNum, pageSize, 0L);
  }
}
