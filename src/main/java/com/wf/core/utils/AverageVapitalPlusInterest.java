package com.wf.core.utils;


import java.math.BigDecimal;

/**
 * 等额本息工具
 * 月均还款:b＝a×i×(1＋i)^n÷((1＋i)^n－1)
 * 支付利息:Y＝n×a×i×(1＋i)^n÷((1＋i)^n－1)－a
 * 还款总额:n×a×i×(1＋i)^n÷((1＋i)^n－1)
 *
 * @author yanlichong
 * @date 2016年8月12日
 */
public class AverageVapitalPlusInterest {

    /**
     * 等额本息计算获取还款方式为等额本息的每月偿还本金和利息
     * <p/>
     * 公式：每月偿还本息=(贷款本金×月利率×(1＋月利率)＾还款月数)÷((1＋月利率)＾还款月数-1)
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalmonth 还款总月数
     * @return 每月偿还本金和利息 四舍六入五成双
     */
    public static double getPerMonthPrincipalInterest(double invest, double yearRate, int totalmonth) {
        //月利率
        BigDecimal monthRate = BigDecimal.valueOf(yearRate).divide(BigDecimal.valueOf(12), 40, BigDecimal.ROUND_HALF_EVEN);
        //每月偿还本息=(贷款本金×月利率×(1＋月利率)＾还款月数)÷((1＋月利率)＾还款月数-1)
        BigDecimal monthIncome = BigDecimal.valueOf(invest).multiply(monthRate).multiply(BigDecimal.valueOf(1).add(monthRate).pow(totalmonth))
                .divide(BigDecimal.valueOf(1).add(monthRate).pow(totalmonth).subtract(BigDecimal.valueOf(1)), 40, BigDecimal.ROUND_HALF_EVEN);

//		System.out.println(monthIncome);
        //四舍六入五成双
        return monthIncome.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }


    public static void main(String[] args) {
        double d = getPerMonthPrincipalInterest(200000, 0.0445, 24);
        System.out.println(d);
        System.out.println(1 / 97.0);
        System.out.println(0.2f + 0.4f
        );
    }
}
