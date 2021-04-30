//package com.birdplanet.service.impl;
//
//import com.alipay.api.AlipayApiException;
//import com.birdplanet.service.IIdGenerateService;
//import com.birdplanet.service.pay.IAlipayService;
//import java.math.BigDecimal;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: AlipayServiceImplTest
// * @date 2019/9/15 17:03
// */
//@SpringBootTest
//class AlipayServiceImplTest {
//
//  @Autowired IAlipayService alipayService;
//  @Autowired IIdGenerateService idGenerateService;
//
//  @Test
//  void refund() throws AlipayApiException {
//
//    //String trade_no = "2019091522001458531051452263",
//    //    ordersn = "46995828828536832",
//    String trade_no = "2019091522001458531051439798",
//        ordersn = "47001035725602816",
//        refund_reason = "",
//        refundId = idGenerateService.ordersn() + "";
//    BigDecimal refund_amount = new BigDecimal("1.00");
//    alipayService.refund(trade_no, ordersn, refund_amount, refund_reason, refundId);
//  }
//}
