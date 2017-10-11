package com.jdd.lkcz.fm.core.utils;

/**
 * Created by zk on 2017/6/23.
 */
public class GenInvitationCodeUtil {

    public static String genInvitationCode(Long id) {
        String invitationCode = "";
        if (id == null)
            return invitationCode;
        if (id > 0 && id <= 999999) {
            String idStr = String.valueOf(id);
            for (int i = 0; i < (7 - idStr.length()); i++) {
                invitationCode += "0";
            }
            invitationCode += idStr;
        } else if (id >= 1000000) {
            invitationCode = String.valueOf(id);
        } else {
            return invitationCode;
        }
        return invitationCode;
    }
}
