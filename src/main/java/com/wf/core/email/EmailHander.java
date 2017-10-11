//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.email;

import javax.mail.MessagingException;

public interface EmailHander {
    void sendText(String var1, String var2, String var3);

    void sendHtml(String var1, String var2, String var3) throws MessagingException;
}
