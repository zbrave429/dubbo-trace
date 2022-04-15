package com.brave.dubbo.trace.model;

/**
 * trace 扩展数据
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-04-15 12:26
 */
public class TraceExtend {

    /**
     * 是否测试流量
     */
    private boolean test;

    /**
     * 当前请求的token信息
     */
    private String token;

    /**
     * 登录用户信息
     */
    private UserInfo userInfo;

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
