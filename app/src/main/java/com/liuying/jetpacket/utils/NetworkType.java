package com.liuying.jetpacket.utils;

/**
 * author : leixin
 * e-mail : xxx@xx
 * time : 2020/03/16
 * desc : 网络连接类型.
 * version: 1.0
 */
public enum NetworkType {
    NETWORK_WIFI("WiFi"),
    NETWORK_4G("4G"),
    NETWORK_2G("2G"),
    NETWORK_3G("3G"),
    NETWORK_UNKNOWN("Unknown"),
    NETWORK_NO("No network");

    private String desc;
    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
