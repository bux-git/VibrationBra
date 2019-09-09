package com.example.vibrationbra.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.vibrationbra.BuildConfig;
import com.example.vibrationbra.MyApplication;
import com.example.vibrationbra.R;
import com.example.vibrationbra.bean.DeviceBean;
import com.example.vibrationbra.localdata.AppParams;
import com.example.vibrationbra.localdata.Constant;
import com.example.vibrationbra.localdata.EventType;
import com.hwangjr.rxbus.RxBus;

import java.util.List;

/**
 * @description：
 * @author：bux on 2019/9/6 9:43
 * @email: 471025316@qq.com
 */
public class BlueUtils {

    public static BlueUtils sUtils;

    private BlueUtils() {

    }

    public static BlueUtils instance() {
        if (sUtils == null) {
            sUtils = new BlueUtils();
            RxBus.get().register(sUtils);
        }
        return sUtils;
    }


    /**
     * 蓝牙初始化
     */
    public  void initGlobal() {
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
    public  boolean isSupportBle() {
        return BleManager.getInstance().isSupportBle();
    }

    /**
     * 检测，打开蓝牙
     */

    public  boolean isBlueEnable() {
        return BleManager.getInstance().isBlueEnable();
    }

    /**
     * 打开蓝牙
     *
     * @param activity
     * @param requestCode
     */
    public  void openBlue(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 关闭蓝牙 释放资源
     */
    public  void destroy() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    public  void cancelScan() {
        BleManager.getInstance().cancelScan();
    }

    /**
     * 获取当前连接设备
     *
     * @return
     */
    public  BleDevice getConnectedDevice() {
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
    public  boolean getDeviceConnected() {
        BleDevice bleDevice = getConnectedDevice();
        if (bleDevice == null) {
            return false;
        }
        BleManager.getInstance().getConnectState(bleDevice);
        return BleManager.getInstance().isConnected(bleDevice);
    }

    public  void connect(BleDevice bleDevice, String mac, BleGattCallback callback) {
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
    public  void disconnect(BleDevice bleDevice) {
        if (isConnect(bleDevice)) {
            BleManager.getInstance().disconnect(bleDevice);
        }
    }

    /**
     * 当前设备连接
     *
     * @param bleDevice
     * @return
     */
    public  boolean isConnect(BleDevice bleDevice) {
        return BleManager.getInstance().isConnected(bleDevice);
    }


    /**
     *
     */
    public  void scanBlue(BleScanCallback bleScanCallback) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(Constant.UUID_SEARCH)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, Constant.BLUE_NAME)   // 只扫描指定广播名的设备，可选
                .setDeviceMac("")                  // 只扫描指定mac的设备，可选
                .setAutoConnect(Constant.AUTO_CONNECT)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(Constant.SCAN_TIME_OUT)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        BleManager.getInstance().scan(bleScanCallback);
    }

    public void connectBle(final BleDevice bleDevice, String mac){
        BleGattCallback callback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                RxBus.get().post(EventType.BLE_START_CONNECT,new Object());

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                RxBus.get().post(EventType.BLE_START_FAIL,bleDevice);
                ToastUtils.showLong(R.string.connect_fail);

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                RxBus.get().post(EventType.BLE_START_SUCCESS,bleDevice);
                //存储连接成功得设备
                AppParams.setDeviceBean(new DeviceBean(bleDevice.getName(), bleDevice.getMac()));
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                // 调用 isconnect 主动断开
                if (isActiveDisConnected) {
                    //ToastUtils.showLong(R.string.active_disconnected);
                } else {
                    ToastUtils.showLong(R.string.disconnected);
                }
                //断开通知
                RxBus.get().post(EventType.BLE_DIS_CONNECTION, bleDevice);

            }
        };
       connect(bleDevice, mac, callback);
    }

    /**
     * 向对应设备 服务 特征写入数据
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     * @param data
     * @param callback
     */
    public  void write(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite, byte[] data
            , BleWriteCallback callback) {
        BleManager.getInstance().write(
                bleDevice,
                uuidService,
                uuidCharacteristicWrite,
                data,
                callback);
    }


    /**
     * 向对应设备 服务 读取特征 数据
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     * @param callback
     */
    public  void read(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite, BleReadCallback callback) {
        BleManager.getInstance().read(
                bleDevice,
                uuidService,
                uuidCharacteristicWrite,
                callback);
    }

    /**
     * 接收notify通知,收到通知后不会回应，
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     * @param callback
     */
    public  void notify(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite, BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                bleDevice,
                uuidService,
                uuidCharacteristicWrite,
                callback);
    }

    /**
     * 关闭notify
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     */
    public  void closeNotify(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite) {
        BleManager.getInstance().stopNotify(bleDevice, uuidService, uuidCharacteristicWrite);
    }

    /**
     * 接收indicate通知,indicate底层封装了应答机制，如果没有收到中央设备的回应，会再次发送直至成功
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     * @param callback
     */
    public  void indicate(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite, BleIndicateCallback callback) {
        BleManager.getInstance().indicate(
                bleDevice,
                uuidService,
                uuidCharacteristicWrite,
                callback);
    }

    /**
     * 关闭indicate
     *
     * @param bleDevice
     * @param uuidService
     * @param uuidCharacteristicWrite
     */
    public  void closeIndicate(BleDevice bleDevice, String uuidService, String uuidCharacteristicWrite) {
        BleManager.getInstance().stopIndicate(bleDevice, uuidService, uuidCharacteristicWrite);
    }
}
