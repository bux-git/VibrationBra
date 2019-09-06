package com.example.vibrationbra;

import java.util.UUID;

/**
 * @description：常量类
 * @author：bux on 2019/9/3 15:42
 * @email: 471025316@qq.com
 */
public class Constant {

    /**
     * 蓝牙搜索条件设置
     * 服务UUID
     */
    public static final UUID[] UUID_SEARCH = null;
    //广播名称
    public static final String[] BLUE_NAME = {""};
    //连接后断开自动重连
    public static final boolean AUTO_CONNECT = false;
    //扫描超时
    public static final long SCAN_TIME_OUT = 10*1000;

    //头码状态
    public static final int HEAD_CODE = 0XAA;

    //力度档位
    public static final int MIN_GEAR = 0X01;
    public static final int MAX_GEAR = 0X05;
    public static final int DEFAULT_GEAR = 0X03;
    public static final int WAIT_GEAR = 0X00;

    //定时 时间限制
    public static final int MIN_TIME = 0X01;
    public static final int MAX_TIME = 0XFF;
    //默认时间
    public static final int DEFAULT_TIME = 0X0F;
    public static final int TIME_WAIT = 0X00;


    //机器状态
    public static class MachineState {
        //关机
        public static final int OFF = 0X00;
        //开机
        public static final int ON = 0X01;
    }

    //模式
    public static class Model {
        //默认无选中
        public static final int mode_default = 0x00;
        //1-捶打
        public static final int mode1 = 0X01;
        //2-挤压
        public static final int mode2 = 0X02;
        //3-提伸
        public static final int mode3 = 0x03;
        //4-推拿
        public static final int mode4 = 0x04;
        //5-揉捏
        public static final int mode5 = 0x05;
        //6- 循环
        public static final int mode6 = 0x06;
    }


    //开启 位置
    public static class Position {
        public static final int LEFT_ON = 0X01;
        public static final int RIGHT_ON = 0X02;
        public static final int ALL_ON = 0x03;
        public static final int POS_WAIT = 0X00;
    }


}
