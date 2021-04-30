//package com.birdplanet.service.impl;
//
//import com.birdplanet.commons.constant.MQConstant;
//import com.birdplanet.service.IMessageQueueService;
//import java.time.LocalDateTime;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.AmqpException;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessagePostProcessor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: MessageQueueServiceImpl
// * @date 2019/12/18 18:36
// */
//@Slf4j
//@Service
//public class MessageQueueServiceImpl extends BaseService implements IMessageQueueService {
//
//@Autowired protected RabbitTemplate rabbitTemplate;
//
//@Override public void send(String queueName, String msg) {
//  log.debug("消息发送时间 :: {}", LocalDateTime.now());
//  rabbitTemplate.convertAndSend(MQConstant.DELAY_EXCHANGE, queueName, msg,
//      message -> {
//        message.getMessageProperties().setHeader("x-delay", 3000);
//        return message;
//      });
//}
//}
