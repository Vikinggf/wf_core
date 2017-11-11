//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.email;

import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import java.util.List;

public interface EmailHander {
    /**
     * 发送html格式邮件
     *
     * @param to      接收人
     * @param subject 主题
     * @param text    邮件内容
     */
    void sendText(String to, String subject, String text);

    /**
     * 发送html格式邮件
     *
     * @param to      接收人
     * @param subject 主题
     * @param html    内容
     * @throws MessagingException
     */
    void sendHtml(String to, String subject, String html) throws MessagingException;

    /**
     * 发送html格式邮件，带附件
     *
     * @param to      接收人
     * @param subject 主题
     * @param html    内容
     * @param attachMents 附件
     * @throws MessagingException
     */
    void sendHtml(String to, String subject, String html, List<AttachMent> attachMents) throws MessagingException;



}


