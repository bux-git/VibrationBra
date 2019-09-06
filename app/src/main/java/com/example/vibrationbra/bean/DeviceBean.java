package com.example.vibrationbra.bean;

/**
 * @description：
 * @author：bux on 2019/9/6 15:14
 * @email: 471025316@qq.com
 */
public class DeviceBean {
    private String name;
    private String mac;


    public DeviceBean(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
