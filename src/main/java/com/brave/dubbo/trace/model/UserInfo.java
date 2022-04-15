package com.brave.dubbo.trace.model;

/**
 * 用户信息
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-04-15 14:17
 */

public class UserInfo {

    private String userName;

    private Long userId;

    private Long merchantId;

    private Long shopId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
