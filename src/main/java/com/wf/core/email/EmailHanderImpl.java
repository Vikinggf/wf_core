//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.email;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class EmailHanderImpl implements InitializingBean, EmailHander {

    private static Logger logger = LoggerFactory.getLogger(EmailHanderImpl.class);

    @Autowired
    private PropertiesFactoryBean propertiesFactoryBean;
    private JavaMailSenderImpl mailSender;
    private String username;
    private String password;
    private Integer port;
    private String host;

    public EmailHanderImpl() {
    }

    @Override
    public void sendText(String to, String subject, String text) {
        try {
            Assert.notNull(this.username, "缺少邮件配置项email.username");
            Assert.notNull(this.password, "缺少邮件配置项email.password");
            Assert.notNull(this.host, "缺少邮件配置项email.host");
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(this.username);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(text);
            this.mailSender.send(mail);
        } catch (MailException e) {
            logger.error("{}-{}-{}", "EmailHander", "sendText", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void sendHtml(String to, String subject, String html) throws MessagingException {
        try {
            Assert.notNull(this.username, "缺少邮件配置项email.username");
            Assert.notNull(this.password, "缺少邮件配置项email.password");
            Assert.notNull(this.host, "缺少邮件配置项email.host");
            MimeMessage mailMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
            messageHelper.setTo(to);
            messageHelper.setFrom(this.username);
            messageHelper.setSubject(subject);
            messageHelper.setText(html, true);
            this.mailSender.send(mailMessage);
        } catch (MailException e) {
            logger.error("{}-{}-{}", "EmailHander", "sendHtml", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void sendHtml(String to, String subject, String html, List<AttachMent> attachMents) throws MessagingException {
        try {
            Assert.notNull(username, "缺少邮件配置项email.username");
            Assert.notNull(password, "缺少邮件配置项email.password");
            Assert.notNull(host, "缺少邮件配置项email.host");
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
            messageHelper.setTo(to);
            messageHelper.setFrom(username);
            messageHelper.setSubject(subject);
            //启动HTML格式的邮件
            messageHelper.setText(html, true);

            if (null != attachMents && attachMents.size() > 0) {
                for (AttachMent item : attachMents) {
                    if (item.getName() != null && !"".equals(item.getName())
                            && item.getName().contains("jpg")) {
                        messageHelper.addInline(item.getName(), item.getFile());
                        continue;
                    }
                    messageHelper.addAttachment(item.getName(), item.getFile());
                }
            }
            //发送邮件
            mailSender.send(mailMessage);
        } catch (MailException e) {
            logger.error("{}-{}-{}", "EmailHander", "sendHtml-attach", ExceptionUtils.getStackTrace(e));
        }
    }

    public void afterPropertiesSet() throws Exception {
        Properties properties = this.propertiesFactoryBean.getObject();
        this.host = properties.getProperty("email.host");
        this.port = properties.getProperty("email.port") == null ? 25 : Integer.valueOf(properties.getProperty("email.port")).intValue();
        this.username = properties.getProperty("email.username");
        this.password = properties.getProperty("email.password");
        this.mailSender = new JavaMailSenderImpl();
        this.mailSender.setHost(this.host);
        this.mailSender.setDefaultEncoding("UTF-8");
        this.mailSender.setUsername(this.username);
        this.mailSender.setPassword(this.password);
        this.mailSender.setPort(this.port.intValue());
    }
}
