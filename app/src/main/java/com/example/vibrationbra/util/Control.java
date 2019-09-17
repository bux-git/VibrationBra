package com.example.vibrationbra.util;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.example.vibrationbra.bean.ControlBean;
import com.example.vibrationbra.localdata.AppParams;
import com.example.vibrationbra.localdata.Constant;

import java.util.Arrays;

/**
 * description：设备指令 请求控制类
 * author：bux on 2019/9/9 15:33
 * email: 471025316@qq.com
 */
public class Control {

    private static final String TAG = "Control";

    /**
     * 解析组合请求报文并发送
     */
    public static void generateReqData() {
        ControlBean cb = AppParams.sControlBean;
        int p = cb.getPos();
        int t = cb.getTime();
        int g = cb.getGear();

        //未选中时直接进入开机待机状态
        if (cb.getMode() == Constant.Model.mode_default) {
            p = Constant.Position.POS_WAIT;
            t = Constant.TIME_WAIT;
            g = Constant.WAIT_GEAR;
        }

        int preKey = cb.getHeadCode() * 2 + cb.getState() + cb.getMode() + g + p + t;


        byte[] reqData = {
                (byte) cb.getHeadCode(), (byte) cb.getHeadCode(),//头码
                (byte) cb.getState(),//状态码
                (byte) cb.getMode(),//模式
                (byte) g,//档位
                (byte) p,//边
                (byte) t,//定时时间
                0, 0,//预留
                (byte) preKey//校验码
        };
        Log.d(TAG, Arrays.toString(reqData));
        BlueUtils.sUtils.write(BlueUtils.sUtils.getConnectedDevice(), Constant.UUID_SERVICE, Constant.UUID_CHARACTERISTIC, reqData, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                ToastUtils.showShort("发送数据到设备成功");
                //本地存储
                AppParams.setControlBean(cb);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                // 发送数据到设备失败
                ToastUtils.showShort("发送数据到设备失败");
            }
        });

    }
}
