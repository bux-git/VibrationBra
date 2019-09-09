package com.example.vibrationbra.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.example.vibrationbra.R;
import com.example.vibrationbra.base.BaseSysBleCheckActivity;
import com.example.vibrationbra.localdata.AppParams;
import com.example.vibrationbra.localdata.EventType;
import com.example.vibrationbra.util.BlueUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @description：
 * @author：bux on 2019/9/6 10:36
 * @email: 471025316@qq.com
 */
public class BlueSearchActivity extends BaseSysBleCheckActivity {
    private static final String TAG = "BlueSearchActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_loading)
    ImageView mIvLoading;
    @BindView(R.id.llt_search)
    LinearLayout mLltSearch;
    @BindView(R.id.tv_scan)
    TextView mTvScan;
    @BindView(R.id.img_blue)
    ImageView mImgBlue;
    @BindView(R.id.txt_name)
    TextView mTxtName;
    @BindView(R.id.txt_mac)
    TextView mTxtMac;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.llt_connected)
    LinearLayout mLltConnected;

    private ProgressDialog progressDialog;
    private boolean isConnecting = false;

    private Animation operatingAnim;

    BaseQuickAdapter<BleDevice, BaseViewHolder> mAdapter;
    List<BleDevice> mBleDevices;
    private BleScanCallback mBleScanCall;

    //已经连接的设备
    private BleDevice bleConnect;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_blue_search_layout;
    }

    @Override
    protected void init() {
        initView();
        initRecyclerView();
        setCurConnectDevice();
        startSearchBlue();
    }

    @OnClick({R.id.llt_search, R.id.rlt_device_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llt_search:
                if (isSearchIng()) {
                    BlueUtils.sUtils.cancelScan();
                } else {
                    startSearchBlue();
                }
                break;
            case R.id.rlt_device_item:
                connect(null, mTxtMac.getText().toString());
                break;
            default:
        }

    }


    @Override
    protected void onDestroy() {
        if (isSearchIng()) {
            BlueUtils.sUtils.cancelScan();
        }
        super.onDestroy();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("搜索设备");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("连接中....");
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());


    }


    private void initRecyclerView() {
        mBleDevices = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new BaseQuickAdapter<BleDevice, BaseViewHolder>(R.layout.adapter_device, mBleDevices) {
            @Override
            protected void convert(BaseViewHolder helper, BleDevice item) {
                //img_blue txt_name txt_mac tv_status
                helper.setText(R.id.txt_name, item.getName())
                        .setText(R.id.txt_mac, item.getMac());

            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                connect(mBleDevices.get(position), null);

            }
        });

    }

    /**
     * 连接设备 只允许连接一个蓝牙设备
     *
     * @param bleDevice
     * @param mac
     */
    private void connect(final BleDevice bleDevice, String mac) {
        //当前有正在连接得蓝牙，直接返回，一次只能有一个处于连接中
        if (isConnecting) {
            return;
        }
        bleConnect = BlueUtils.sUtils.getConnectedDevice();

        //当前连接设备已经连接
        if (bleConnect != null) {
            if ((!TextUtils.isEmpty(mac) && bleConnect.getMac().equals(mac))
                    || bleConnect.getKey().equals(bleDevice.getKey())) {
                //已经连接得设备点击
                showDialog("是否断开当前设备", "即将断开当前设备的蓝牙连接", "取消", "断开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BlueUtils.sUtils.disconnect(bleConnect);
                    }
                });
                return;
            }
        }

        //停止搜索
        stopScanLoading();
        BlueUtils.sUtils.cancelScan();
        BlueUtils.sUtils.connectBle(bleDevice,mac);

    }


    /**
     * 设置已经配对过得设备
     */
    private void setCurConnectDevice() {
        if (mLltConnected == null) {
            return;
        }
        //配对过得设备
        BleDevice bleDevice = BlueUtils.sUtils.getConnectedDevice();
        if (bleDevice == null) {
            //历史配对记录
            if (AppParams.sDeviceBean == null) {
                mLltConnected.setVisibility(View.GONE);
            } else {
                mLltConnected.setVisibility(View.VISIBLE);
                setConnectHis(AppParams.sDeviceBean.getName(), AppParams.sDeviceBean.getMac(), R.color.black, R.string.no_connect, R.color.holo_light);
            }
        } else {
            //当前已经有连接设备
            mLltConnected.setVisibility(View.VISIBLE);

            setConnectHis(bleDevice.getName()
                    , bleDevice.getMac()
                    , BlueUtils.sUtils.getDeviceConnected() ? R.color.colorAccent : R.color.black
                    , BlueUtils.sUtils.getDeviceConnected() ? R.string.connected : R.string.no_connect
                    , BlueUtils.sUtils.getDeviceConnected() ? R.color.colorAccent : R.color.holo_light);
        }
    }


    /**
     * 设置已经配对得设备
     *
     * @param name
     * @param mac
     * @param color
     * @param status
     * @param statusColor
     */
    private void setConnectHis(String name, String mac, int color, int status, int statusColor) {
        color = getResources().getColor(color);
        mImgBlue.setColorFilter(color);

        mTxtName.setText(name);
        mTxtName.setTextColor(color);

        mTxtMac.setText(mac);
        mTxtMac.setTextColor(color);

        mTvStatus.setText(status);
        mTvStatus.setTextColor(getResources().getColor(statusColor));
        mTvStatus.setVisibility(View.VISIBLE);
    }


    /**
     * 设置搜索条件
     */
    private void startSearchBlue() {
        checkBlue(new OnCheckResult() {
            @Override
            public void onSuccess() {
                if (mBleScanCall == null) {
                    mBleScanCall = new BleScanCallback() {
                        @Override
                        public void onScanStarted(boolean success) {
                            mBleDevices.clear();
                            mAdapter.notifyDataSetChanged();
                            startScanLoading();
                        }

                        @Override
                        public void onLeScan(BleDevice bleDevice) {
                            super.onLeScan(bleDevice);
                        }

                        @Override
                        public void onScanning(BleDevice bleDevice) {
                            if (mIvLoading != null) {
                                if (BlueUtils.sUtils.isConnect(bleDevice)) {
                                    return;
                                }
                                mBleDevices.add(bleDevice);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onScanFinished(List<BleDevice> scanResultList) {
                            stopScanLoading();


                        }
                    };
                }
                BlueUtils.sUtils.scanBlue(mBleScanCall);
            }
        });

    }


    private void stopScanLoading() {
        if (mIvLoading != null) {
            mIvLoading.clearAnimation();
            mTvScan.setText(getString(R.string.start_scan));
        }
    }

    private void startScanLoading() {
        mIvLoading.startAnimation(operatingAnim);
        mTvScan.setText(getString(R.string.stop_scan));
    }

    /**
     * 当前是否再扫描
     *
     * @return
     */
    private boolean isSearchIng() {
        return mTvScan.getText().equals(getString(R.string.stop_scan));
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(EventType.BLE_START_CONNECT)})
    public void onStartConnect(Object o) {
        isConnecting = true;
        progressDialog.show();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(EventType.BLE_START_FAIL)})
    public void onConnectFail(BleDevice bleDevice) {
        isConnecting = false;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(EventType.BLE_START_SUCCESS)})
    public void onConnectSuccess(BleDevice bleDevice) {
        isConnecting = false;
        progressDialog.dismiss();
        //设置连接过得设备
        setCurConnectDevice();
        mBleDevices.remove(bleDevice);
        mAdapter.notifyDataSetChanged();
        //同时只连接一个设备 将原来连接设备断开
        BlueUtils.sUtils.disconnect(bleConnect);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(EventType.BLE_DIS_CONNECTION)})
    public void onDisConnected(BleDevice bleDevice) {
        isConnecting = false;
        //特指连接后再断开的情况
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //设置连接过得设备
        setCurConnectDevice();
    }

}
