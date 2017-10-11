//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.email;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

public class EmailHanderImpl implements InitializingBean, EmailHander {
    @Autowired
    private PropertiesFactoryBean propertiesFactoryBean;
    private JavaMailSenderImpl mailSender;
    private String username;
    private String password;
    private Integer port;
    private String host;

    public EmailHanderImpl() {
    }

    public void sendText(String to, String subject, String text) {
        Assert.notNull(this.username, "缺少邮件配置项email.username");
        Assert.notNull(this.password, "缺少邮件配置项email.password");
        Assert.notNull(this.host, "缺少邮件配置项email.host");
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(this.username);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        this.mailSender.send(mail);
    }

    public void sendHtml(String to, String subject, String html) throws MessagingException {
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
