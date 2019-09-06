package com.example.vibrationbra;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;

import java.util.List;

/**
 * @description：
 * @author：bux on 2019/9/6 9:43
 * @email: 471025316@qq.com
 */
public class BlueUtils {


    /**
     * 蓝牙初始化
     */
    public static void initGlobal() {
        BleManager.getInstance().init(MyApplication.get());
        BleManager.getInstance()
                .enableLog(BuildConfig.DEBUG)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
    }

    /**
     * 设备是否支持蓝牙
     *
     * @return
     */
    public static boolean isSupportBle() {
        return BleManager.getInstance().isSupportBle();
    }

    /**
     * 检测，打开蓝牙
     */

    public static boolean isBlueEnable() {
        return BleManager.getInstance().isBlueEnable();
    }

    /**
     * 打开蓝牙
     *
     * @param activity
     * @param requestCode
     */
    public static void openBlue(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void destroy() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    public static void cancelScan() {
        BleManager.getInstance().cancelScan();
    }

    /**
     * 获取当前连接设备
     *
     * @return
     */
    public static BleDevice getConnectedDevice() {
        List<BleDevice> bleDevices = BleManager.getInstance().getAllConnectedDevice();
        if (bleDevices == null
                || bleDevices.size() == 0) {
            return null;
        } else {
            return bleDevices.get(0);
        }
    }

    /**
     * 当前设备是否连接
     *
     * @return
     */
    public static boolean getDeviceConnected() {
        BleDevice bleDevice = getConnectedDevice();
        if (bleDevice == null) {
            return false;
        }
        BleManager.getInstance().getConnectState(bleDevice);
        return BleManager.getInstance().isConnected(bleDevice);
    }

    public static void connect(BleDevice bleDevice, String mac, BleGattCallback callback) {
        if (bleDevice != null) {
            BleManager.getInstance().connect(bleDevice, callback);
        } else {
            BleManager.getInstance().connect(mac, callback);
        }
    }

    /**
     * 断开指定设备
     *
     * @param bleDevice
     */
    public static void disconnect(BleDevice bleDevice) {
        if (isConnect(bleDevice)) {
            BleManager.getInstance().disconnect(bleDevice);
        }
    }

    /**
     * 当前设备连接
     * @param bleDevice
     * @return
     */
    public static boolean isConnect(BleDevice bleDevice) {
        return BleManager.getInstance().isConnected(bleDevice);
    }
}
