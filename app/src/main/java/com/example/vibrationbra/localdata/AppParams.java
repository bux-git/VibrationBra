package com.example.vibrationbra.localdata;

import android.text.TextUtils;

import com.example.common.sp.SPUtils;
import com.example.vibrationbra.MyApplication;
import com.example.vibrationbra.bean.ControlBean;
import com.example.vibrationbra.bean.DeviceBean;


/**
 * @description：APP级别卸载才删除
 * @author：bux on 2018/6/25 9:57
 * @email: 471025316@qq.com
 */
public class AppParams {

    private static final String FILE_NAME = "AppParams";
    private static SPUtils sp;

    private static final String CONTROL_KEY = "CONTROL_KEY";
    public static ControlBean sControlBean;

    private static final String DEVICE_KEY = "DEVICE_KEY";
    public static DeviceBean sDeviceBean;

    static {
        sp = SPUtils.getInstance(FILE_NAME, MyApplication.get());
        initControl();
        initDeviceBean();
    }

    public static void setControlBean(ControlBean controlBean) {
        if (controlBean == null) {
            controlBean = new ControlBean();
        }
        sControlBean = controlBean;
        sp.put(CONTROL_KEY, MyApplication.getGson().toJson(controlBean));

    }

    private static void initControl() {
        if (sControlBean == null) {
            String json = sp.getString(CONTROL_KEY, "");
            if (TextUtils.isEmpty(json)) {
                sControlBean = new ControlBean();
                setControlBean(sControlBean);
            } else {
                sControlBean = MyApplication.getGson().fromJson(json, ControlBean.class);
            }
        }
    }

    public static void setDeviceBean(DeviceBean deviceBean) {
        if (deviceBean == null) {
            return;
        }
        sDeviceBean = deviceBean;
        sp.put(DEVICE_KEY, MyApplication.getGson().toJson(sDeviceBean));
    }

    private static void initDeviceBean() {
        if (sDeviceBean == null) {
            String json = sp.getString(DEVICE_KEY, "");
            if (TextUtils.isEmpty(json)) {
                sDeviceBean = null;
            } else {
                sDeviceBean = MyApplication.getGson().fromJson(json, DeviceBean.class);
            }
        }
    }

}
