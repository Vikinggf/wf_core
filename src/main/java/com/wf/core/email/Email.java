package com.wf.core.email;

/**
 * Created by lc on 2018/8/31.
 */
public class Email {
    /** 邮件接收者 **/
    private String to;
    /** 主题 **/
    private String subject;
    /** 发件人别名 **/
    private String personalName;
    /** 内容 **/
    private String content;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
