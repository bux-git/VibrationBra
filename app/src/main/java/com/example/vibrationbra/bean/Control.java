package com.example.vibrationbra.bean;

import android.util.Log;

import com.example.vibrationbra.Constant;

import java.util.Arrays;

/**
 * @description：
 * @author：bux on 2019/9/3 16:07
 * @email: 471025316@qq.com
 */
public class Control {
    private static final String TAG = "Control";
    //头码
    private int headCode = Constant.HEAD_CODE;
    //状态
    private int state = Constant.MachineState.ON;
    //模式
    private int mode = Constant.Model.mode_default;
    //力度
    private int gear = Constant.DEFAULT_GEAR;
    //方位
    private int pos = Constant.Position.ALL_ON;
    //定时时间
    private int time = Constant.DEFAULT_TIME;
    //预留
    private int preByte = 0x00;


    public int getHeadCode() {
        return headCode;
    }

    public void setHeadCode(int headCode) {
        this.headCode = headCode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        checkData();
    }


    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
        checkData();
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
        checkData();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        checkData();
    }

    public int getPreByte() {
        return preByte;
    }

    public void setPreByte(int preByte) {
        this.preByte = preByte;
    }

    private void checkData() {

        generateData();
    }

    private void generateData() {

        int p = this.pos;
        int t = this.time;
        int g = this.gear;

        //未选中时直接进入开机待机状态
        if (mode == Constant.Model.mode_default) {
            p = Constant.Position.POS_WAIT;
            t = Constant.TIME_WAIT;
            g = Constant.WAIT_GEAR;
        }


        byte[] reqData = {
                (byte) headCode, (byte) headCode,//头码
                (byte) state,//状态码
                (byte) mode,//模式
                (byte) g,//档位
                (byte) p,//边
                (byte) t,//定时时间
                0, 0,//预留
                (byte) preByte//校验码
        };
        Log.d(TAG,  toString()+"\r\n"+ Arrays.toString(reqData));
        //本地存储
    }


    @Override
    public String toString() {
        return "Control{" +
                "headCode=" + headCode +
                ", state=" + state +
                ", mode=" + mode +
                ", gear=" + gear +
                ", pos=" + pos +
                ", time=" + time +
                ", preByte=" + preByte +
                '}';
    }
}
