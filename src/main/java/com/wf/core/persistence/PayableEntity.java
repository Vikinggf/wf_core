package com.wf.core.persistence;

/**
 * 支持支付的entity
 *
 * @author Fe 2016年9月27日
 */
public interface PayableEntity {

    public Integer getPayStatus();

    public void setPayStatus(Integer payStatus);

    public Integer getPayType();

    public void setPayType(Integer payType);

    public void setPayRemarks(String payRemarks);

    public String getPayRemarks();

    public Double getOrderMoney();

    public Integer getOrderStatus();

    public String getOrderNo();

    public Long getUserId();

}
