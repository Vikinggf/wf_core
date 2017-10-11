//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.event;

import com.jdd.lkcz.fm.core.event.TaskEvent;

import java.util.Map;

public class BettingTaskEvent extends TaskEvent {
    public static final String QUEUE = "new_game_task_betting";
    private Integer gameType;
    private Long bettingAmount;
    private Long resturnAmount;

    public BettingTaskEvent() {
    }

    public Integer getGameType() {
        return this.gameType;
    }

    public void setGameType(Integer gameType) {
        this.gameType = gameType;
    }

    public Long getBettingAmount() {
        return this.bettingAmount;
    }

    public void setBettingAmount(Long bettingAmount) {
        this.bettingAmount = bettingAmount;
    }

    public Long getResturnAmount() {
        return this.resturnAmount;
    }

    public void setResturnAmount(Long resturnAmount) {
        this.resturnAmount = resturnAmount;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = this.doToMap();
        map.put("gameType", this.gameType);
        map.put("resturnAmount", this.resturnAmount);
        map.put("bettingAmount", this.bettingAmount);
        return map;
    }

    public String getMqQueue() {
        return "new_game_task_betting";
    }
}
