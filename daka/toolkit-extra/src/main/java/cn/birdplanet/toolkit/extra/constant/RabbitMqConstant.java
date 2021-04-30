package cn.birdplanet.toolkit.extra.constant;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: MQConstant
 * @date 2019/12/18 18:38
 */
public class RabbitMqConstant {


  private RabbitMqConstant() {
  }
  // 忘记签到 GameMode
  public static final String GM_FORGOT_CHECKIN_DLX_QUEUE_NAME = "forgot.checkin.game.dlx-queue";
  public static final String GM_FORGOT_CHECKIN_DELAY_EXCHANGE_NAME = "forgot.checkin.game.dlx-exchange";
  public static final String GM_FORGOT_CHECKIN_DLX_ROUTING_KEY_NAME = "forgot.checkin.game.dlx-routing.key";

  // 忘记签到 NormalMode
  public static final String NM_FORGOT_CHECKIN_DLX_QUEUE_NAME = "forgot.checkin.noraml.dlx-queue";
  public static final String NM_FORGOT_CHECKIN_DELAY_EXCHANGE_NAME = "forgot.checkin.noraml.dlx-exchange";
  public static final String NM_FORGOT_CHECKIN_DLX_ROUTING_KEY_NAME = "forgot.checkin.noraml.dlx-routing.key";

  // 忘记签到 RoomMode
  public static final String RM_FORGOT_CHECKIN_DLX_QUEUE_NAME = "forgot.checkin.room.dlx-queue";
  public static final String RM_FORGOT_CHECKIN_DELAY_EXCHANGE_NAME = "forgot.checkin.room.dlx-exchange";
  public static final String RM_FORGOT_CHECKIN_DLX_ROUTING_KEY_NAME = "forgot.checkin.room.dlx-routing.key";


  // 发送短信
  public static final String SMS_QUEUE_NAME = "mail.queue";
  public static final String SMS_EXCHANGE_NAME = "mail.exchange";
  public static final String SMS_ROUTING_KEY_NAME = "mail.routing.key";

  // 发送邮件
  public static final String MAIL_QUEUE_NAME = "mail.queue";
  public static final String MAIL_EXCHANGE_NAME = "mail.exchange";
  public static final String MAIL_ROUTING_KEY_NAME = "mail.routing.key";

  // 登录日志
  public static final String LOGIN_LOG_QUEUE_NAME = "login.log.queue";
  public static final String LOGIN_LOG_EXCHANGE_NAME = "login.log.exchange";
  public static final String LOGIN_LOG_ROUTING_KEY_NAME = "login.log.routing.key";

}
