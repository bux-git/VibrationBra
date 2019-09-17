package com.example.vibrationbra.ui;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.example.common.widget.SeekLayout;
import com.example.vibrationbra.R;
import com.example.vibrationbra.base.BaseSysBleCheckActivity;
import com.example.vibrationbra.localdata.AppParams;
import com.example.vibrationbra.localdata.Constant;
import com.example.vibrationbra.localdata.EventType;
import com.example.vibrationbra.util.BlueUtils;
import com.example.vibrationbra.util.Control;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import butterknife.BindView;

public class MainActivity extends BaseSysBleCheckActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.iv_batter)
    ImageView mIvBatter;
    @BindView(R.id.tv_blue)
    TextView mTvBlue;
    @BindView(R.id.rg_pos)
    RadioGroup mRgPos;
    @BindView(R.id.tv_center)
    TextView mTvCenter;
    @BindView(R.id.tv_mode1)
    TextView mTvMode1;
    @BindView(R.id.tv_mode2)
    TextView mTvMode2;
    @BindView(R.id.tv_mode3)
    TextView mTvMode3;
    @BindView(R.id.tv_mode4)
    TextView mTvMode4;
    @BindView(R.id.tv_mode5)
    TextView mTvMode5;
    @BindView(R.id.skl_time)
    SeekLayout mSklTime;
    @BindView(R.id.slt_gear)
    SeekLayout mSltGear;
    @BindView(R.id.rb_left)
    RadioButton mRbLeft;
    @BindView(R.id.rb_all)
    RadioButton mRbAll;
    @BindView(R.id.rb_right)
    RadioButton mRbRight;
    @BindView(R.id.flt_cover)
    FrameLayout mFltCover;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        //mFltCover.setVisibility(View.GONE);
        //初始化蓝牙设备
        BlueUtils.instance();
        BlueUtils.sUtils.initGlobal();
        setDeviceData();
        setListener();
        mIvBatter.setImageLevel(3);
    }


    /**
     * 设置设备参数
     */
    private void setDeviceData() {
        //设置方向
        switch (AppParams.sControlBean.getPos()) {
            case Constant.Position.LEFT_ON:
                mRbLeft.setChecked(true);
                break;
            case Constant.Position.RIGHT_ON:
                mRbRight.setChecked(true);
                break;
            case Constant.Position.ALL_ON:
                mRbAll.setChecked(true);
                break;
            default:
        }

        //设置模式
        switch (AppParams.sControlBean.getMode()) {
            case Constant.Model.mode_default:
                setSelectModel(null);
                break;
            case Constant.Model.mode1:
                setSelectModel(mTvMode1);
                break;
            case Constant.Model.mode2:
                setSelectModel(mTvMode2);
                break;
            case Constant.Model.mode3:
                setSelectModel(mTvMode3);
                break;
            case Constant.Model.mode4:
                setSelectModel(mTvMode4);
                break;
            case Constant.Model.mode5:
                setSelectModel(mTvMode5);
                break;
            case Constant.Model.mode6:
                setSelectModel(mTvCenter);
                break;
            default:
        }

        //设置时间
        mSklTime.setLimit(Constant.MIN_TIME, Constant.MAX_TIME, "");
        mSklTime.setProgress(AppParams.sControlBean.getTime());
        //设置档位
        mSltGear.setLimit(Constant.MIN_GEAR, Constant.MAX_GEAR, "档位：%1d档");
        mSltGear.setProgress(AppParams.sControlBean.getGear());
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mRgPos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int pos = Constant.Position.POS_WAIT;
                switch (checkedId) {
                    case R.id.rb_left:
                        pos = Constant.Position.LEFT_ON;
                        break;
                    case R.id.rb_all:
                        pos = Constant.Position.ALL_ON;
                        break;
                    case R.id.rb_right:
                        pos = Constant.Position.RIGHT_ON;
                        break;
                    default:
                }
                if (pos == AppParams.sControlBean.getPos()) {
                    return;
                }
                AppParams.sControlBean.setPos(pos);
                sendControlData();

            }
        });
        mTvCenter.setOnClickListener(this);
        mTvMode1.setOnClickListener(this);
        mTvMode2.setOnClickListener(this);
        mTvMode3.setOnClickListener(this);
        mTvMode4.setOnClickListener(this);
        mTvMode5.setOnClickListener(this);
        mTvBlue.setOnClickListener(this);
        mFltCover.setOnClickListener(this);

        mSklTime.setOnProgressChangeLister(new SeekLayout.OnProgressChangeLister() {
            @Override
            public void onProgressChange(int progress) {
                if (progress == AppParams.sControlBean.getTime()) {
                    return;
                }
                AppParams.sControlBean.setTime(progress);
                sendControlData();
            }
        });

        mSltGear.setOnProgressChangeLister(new SeekLayout.OnProgressChangeLister() {
            @Override
            public void onProgressChange(int progress) {
                if (progress == AppParams.sControlBean.getGear()) {
                    return;
                }
                AppParams.sControlBean.setGear(progress);
                sendControlData();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_center:
                AppParams.sControlBean.setMode(Constant.Model.mode6);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_mode1:
                AppParams.sControlBean.setMode(Constant.Model.mode1);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_mode2:
                AppParams.sControlBean.setMode(Constant.Model.mode2);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_mode3:
                AppParams.sControlBean.setMode(Constant.Model.mode3);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_mode4:
                AppParams.sControlBean.setMode(Constant.Model.mode4);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_mode5:
                AppParams.sControlBean.setMode(Constant.Model.mode5);
                setSelectModel(v);
                sendControlData();
                break;
            case R.id.tv_blue:
            case R.id.flt_cover: //已经连接得设备点击

                //去蓝牙链接页面
                checkBlue(new OnCheckResult() {
                    @Override
                    public void onSuccess() {
                        BlueSearchActivity.start(MainActivity.this, BlueSearchActivity.class, null);
                    }
                });
                break;
            default:
        }

    }

    @Override
    protected void onDestroy() {
        BlueUtils.sUtils.destroy();
        super.onDestroy();

    }

    /**
     * 设置模式
     *
     * @param v
     */
    private void setSelectModel(View v) {

        //点击已经选中得
        if (v != null
                && v.isSelected()) {
            //选中已经选中得 则表示待机状态
            v.setSelected(false);
            AppParams.sControlBean.setMode(Constant.Model.mode_default);
            return;
        }

        //未选中得
        mTvMode1.setSelected(false);
        mTvMode2.setSelected(false);
        mTvMode3.setSelected(false);
        mTvMode4.setSelected(false);
        mTvMode5.setSelected(false);
        mTvCenter.setSelected(false);
        if (v != null) {
            v.setSelected(true);
        }

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Control.generateReqData();
        }
    };

    @SuppressLint("CheckResult")
    private void sendControlData() {
        mHandler.removeMessages(1);
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        mHandler.sendMessageDelayed(msg, Constant.CONTROL_INTERVAL);
    }

    /**
     * 断开连接 连接成功
     *
     * @param bleDevice
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(EventType.BLE_DIS_CONNECTION), @Tag(EventType.BLE_START_SUCCESS)})
    public void disConnected(BleDevice bleDevice) {
        if (mTvBlue == null) {
            return;
        }
        //已经连接状态
        if (BlueUtils.sUtils.getDeviceConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ColorStateList csl = getResources().getColorStateList(R.color.colorAccent);
                mTvBlue.setCompoundDrawableTintList(csl);
            }

            mTvBlue.setText(BlueUtils.sUtils.getConnectedDevice().getName());
            mTvBlue.setTextColor(getResources().getColor(R.color.colorAccent));
            mFltCover.setVisibility(View.GONE);
        } else {
            //未连接
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ColorStateList csl = getResources().getColorStateList(R.color.black);
                mTvBlue.setCompoundDrawableTintList(csl);
            }
            mTvBlue.setText(R.string.no_connect);
            mTvBlue.setTextColor(getResources().getColor(R.color.holo_light));
            mFltCover.setVisibility(View.VISIBLE);
        }
    }


}
