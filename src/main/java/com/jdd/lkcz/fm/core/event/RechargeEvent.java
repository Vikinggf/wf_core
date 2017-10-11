//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.event;

import java.util.Map;

public class RechargeEvent extends TaskEvent {
    public static final String QUEUE = "new_game_task_recharge";
    private Long rechargeAmount;

    public RechargeEvent() {
    }

    public Long getRechargeAmount() {
        return this.rechargeAmount;
    }

    public void setRechargeAmount(Long rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Map<String, Object> toMap() {
        return null;
    }

    public String getMqQueue() {
        return "new_game_task_recharge";
    }
}
