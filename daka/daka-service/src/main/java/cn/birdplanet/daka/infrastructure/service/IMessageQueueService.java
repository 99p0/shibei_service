package cn.birdplanet.daka.infrastructure.service;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: IMessageQueueService
 * @date 2019/12/18 18:35
 */
public interface IMessageQueueService {

  /**
   * 发送消息到队列
   *
   * @param queueName 队列名称
   * @param msg 消息内容
   */
  void send(String queueName, String msg);
}
