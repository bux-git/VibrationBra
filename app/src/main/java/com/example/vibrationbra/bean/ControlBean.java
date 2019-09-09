package com.example.vibrationbra.bean;

import com.example.vibrationbra.localdata.Constant;

/**
 * @description：
 * @author：bux on 2019/9/3 16:07
 * @email: 471025316@qq.com
 */
public class ControlBean {
    private static final String TAG = "ControlBean";
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
    }


    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }




    @Override
    public String toString() {
        return "ControlBean{" +
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
