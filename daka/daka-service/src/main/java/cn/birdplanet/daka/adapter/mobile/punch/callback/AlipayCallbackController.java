/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch.callback;

import cn.birdplanet.daka.domain.po.AlipayConfig;
import cn.birdplanet.daka.domain.po.Recharge;
import cn.birdplanet.daka.infrastructure.service.IPayService;
import cn.birdplanet.daka.infrastructure.service.IRechargeService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.toolkit.extra.code.TradeChannelCodes;
import cn.birdplanet.toolkit.extra.code.TradeStatusCodes;
import cn.birdplanet.toolkit.json.JacksonUtil;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AlipayCallbackController
 * @date 2019-07-08 09:47
 */
@Api(tags = "支付宝回调通知 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/callback/alipay")
public class AlipayCallbackController {

  @Autowired private IPayService payService;
  @Autowired private IUserService userService;
  @Autowired private IRechargeService rechargeService;

  @ApiOperation(value = "异步通知", notes = "Notes")
  @PostMapping("notify")
  public String alipay_notify(HttpServletRequest request) throws Exception {
    // 获取支付宝POST过来反馈信息
    Map<String, String> params = new HashMap<>(16);
    Map requestParams = request.getParameterMap();
    for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
      String name = (String) iter.next();
      String[] values = (String[]) requestParams.get(name);
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        valueStr = (i == values.length - 1) ? valueStr + values[i]
            : valueStr + values[i] + ",";
      }
      //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
      //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
      params.put(name, valueStr);
    }
    //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
    //商户订单号 ::
    String ordersn = request.getParameter("out_trade_no");
    //支付宝交易号
    String trade_no = request.getParameter("trade_no");
    //交易状态
    String trade_status = request.getParameter("trade_status");
    // 只有返回交易成功，方才处理！！！
    if (!trade_status.equals("TRADE_SUCCESS")){
      return "success";
    }
    //
    String appId = request.getParameter("app_id");
    if (StringUtils.isBlank(appId)) {
      log.debug("参数缺失 params: {}", params);
      return "fail";
    }
    AlipayConfig alipay = payService.getAlipayConfig(appId);
    if (null == alipay) {
      log.error("不存在此支付宝应用 params: {}", params);
      return "fail";
    }
    boolean verify_result =
        AlipaySignature.rsaCheckV1(params, alipay.getPublicKeyAli(), alipay.getCharset(),
            alipay.getSignType());
    //验证成功
    if (verify_result) {
      boolean flag;
      // 用户充值成功
      Recharge recharge = rechargeService.getByOrdersn(ordersn);
      if (null == recharge) {
        log.error("订单不存在，ordersn: {}", ordersn);
        return "fail";
      }
      synchronized (recharge) {
        //            交易结束，不可退款
        if (trade_status.equals("TRADE_FINISHED")) {
          // 判断该笔订单是否在商户网站中已经做过处理
          // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
          // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
          // 如果有做过处理，不执行商户的业务程序

          //注意：
          //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
          //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
        } else if (trade_status.equals("WAIT_BUYER_PAY")) {
          // 交易创建，等待买家付款

        } else if (trade_status.equals("TRADE_CLOSED")) {
          // 未付款交易超时关闭，或支付完成后全额退款

        } else if (trade_status.equals("TRADE_SUCCESS")) {
          // 交易支付成功
          // 判断该笔订单是否在商户网站中已经做过处理
          // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
          // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
          // 如果有做过处理，不执行商户的业务程序
          //注意：
          // 如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
          // 更新余额

          if (recharge.getTradeStatus()
              .equalsIgnoreCase(TradeStatusCodes.WAIT_BUYER_PAY.getCode())) {

            flag = userService.rechargeSuccess(TradeChannelCodes.alipay, recharge.getAmount(),
                recharge.getUid());
            log.debug("更新用户「{}」余额增加 +{} >>> {}", recharge.getUid(), recharge.getAmount(), flag);
          }
        }
        flag = rechargeService.updateByIdWithRechargeSucc(recharge.getId(), trade_no, trade_status,
            JacksonUtil.obj2Json(params));
        log.debug("更新充值状态: {}", flag);
        return "success";
      }
    } else {
      //验证失败
      log.debug("验证失败: {}", params);
      return "fail";
    }
  }
}
