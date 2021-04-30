/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.infrastructure.service.impl;

import cn.birdplanet.daka.infrastructure.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailServiceImpl implements IMailService {

  //@Autowired private TemplateEngine templateEngine;
  //
  //@Qualifier(value = "noreplyMailSender")
  //@Autowired private JavaMailSenderImpl noreplyMailSender;
  //
  //@Async("taskExecutor")
  //private void sendNoreplyMail(String mail, String subject, String body) {
  //  try {
  //    noreplyMailSender.send(mimeMessage -> {
  //      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
  //      // 设置邮件主题
  //      helper.setSubject(subject);
  //      // 设置邮件发送者
  //      helper.setFrom(
  //          new InternetAddress(MimeUtility.encodeText("小鸟星球") + " <no-reply@birdplanet.cn>"));
  //      // 设置邮件接收者，可以有多个接收者
  //      helper.setTo(mail.split(","));
  //      // 设置邮件抄送人，可以有多个抄送人
  //      //helper.setCc("");
  //      //设置隐秘抄送人，可以有多个
  //      //helper.setBcc("");
  //
  //      Context context = new Context();
  //      context.setVariable("username", "javaboy");
  //      context.setVariable("num", "000001");
  //      context.setVariable("salary", "99999");
  //      String process = templateEngine.process("notify.html", context);
  //
  //      helper.setText(process, true);
  //    });
  //  } catch (Exception e) {
  //    log.error("通知邮件发送失败: {}, subject:{}，content:{}", e, subject, body);
  //  }
  //}
}
