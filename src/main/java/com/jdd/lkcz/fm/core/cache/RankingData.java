package com.jdd.lkcz.fm.core.cache;

/**
 * 排行榜数据
 * Created by Chris on 2016/12/26.
 */
public class RankingData {
    private Object element;
    private double score;

    public RankingData(Object element, double score) {
        this.element = element;
        this.score = score;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
