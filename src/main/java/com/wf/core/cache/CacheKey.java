package com.wf.core.cache;

/**
 * 缓存key
 * @author Fe 2016年4月16日
 */
public interface CacheKey {
    long MINUTE_1 = 60L * 1;
    long MINUTE_5 = MINUTE_1 * 5;
    long HOUR_1 = MINUTE_1 * 60;
    long HOUR_2 = HOUR_1 * 2;
    long HOUR_3 = HOUR_1 * 3;
    long HOUR_5 = HOUR_1 * 5;
    long HOUR_12 = HOUR_1 * 12;
    long DAY_1 = HOUR_1 * 24;
    long DAY_2 = DAY_1 * 2;
    long DAY_3 = DAY_1 * 3;
    long DAY_5 = DAY_1 * 5;
    long DAY_15 = DAY_1 * 15;
    long WEEK_1 = DAY_1 * 7;
    long WEEK_2 = WEEK_1 * 2;
    long MONTH_1 = DAY_1 * 30;
    long MONTH_2 = MONTH_1 * 2;
    long MONTH_3 = MONTH_1 * 3;

    public String key();
}
