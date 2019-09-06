package com.example.vibrationbra;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.example.common.util.observer.Observer;
import com.example.common.util.observer.ObserverManager;
import com.example.common.widget.SeekLayout;
import com.example.vibrationbra.base.BaseSysBleCheckActivity;
import com.example.vibrationbra.localdata.AppParams;

import butterknife.BindView;

public class MainActivity extends BaseSysBleCheckActivity implements View.OnClickListener, Observer {
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
        ObserverManager.getInstance().addObserver(this);
        //初始化蓝牙设备
        BlueUtils.initGlobal();
        setDeviceData();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBlueData();
    }

    private void setBlueData() {
        if (mTvBlue == null) {
            return;
        }
        //已经连接状态
        if (BlueUtils.getDeviceConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ColorStateList csl = getResources().getColorStateList(R.color.colorAccent);
                mTvBlue.setCompoundDrawableTintList(csl);
            }

            mTvBlue.setText(BlueUtils.getConnectedDevice().getName());
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

    /**
     * 设置设备参数
     */
    private void setDeviceData() {
        //设置方向
        switch (AppParams.sControl.getPos()) {
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
        switch (AppParams.sControl.getMode()) {
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
        mSklTime.setProgress(AppParams.sControl.getTime());
        //设置档位
        mSltGear.setLimit(Constant.MIN_GEAR, Constant.MAX_GEAR, "档位：%1d档");
        mSltGear.setProgress(AppParams.sControl.getGear());
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mRgPos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_left:
                        AppParams.sControl.setPos(Constant.Position.LEFT_ON);
                        break;
                    case R.id.rb_all:
                        AppParams.sControl.setPos(Constant.Position.ALL_ON);
                        break;
                    case R.id.rb_right:
                        AppParams.sControl.setPos(Constant.Position.RIGHT_ON);
                        break;
                    default:
                }

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
                AppParams.sControl.setTime(progress);
            }
        });

        mSltGear.setOnProgressChangeLister(new SeekLayout.OnProgressChangeLister() {
            @Override
            public void onProgressChange(int progress) {
                AppParams.sControl.setGear(progress);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_center:
                AppParams.sControl.setMode(Constant.Model.mode6);
                setSelectModel(v);
                break;
            case R.id.tv_mode1:
                AppParams.sControl.setMode(Constant.Model.mode1);
                setSelectModel(v);
                break;
            case R.id.tv_mode2:
                AppParams.sControl.setMode(Constant.Model.mode2);
                setSelectModel(v);
                break;
            case R.id.tv_mode3:
                AppParams.sControl.setMode(Constant.Model.mode3);
                setSelectModel(v);
                break;
            case R.id.tv_mode4:
                AppParams.sControl.setMode(Constant.Model.mode4);
                setSelectModel(v);
                break;
            case R.id.tv_mode5:
                AppParams.sControl.setMode(Constant.Model.mode5);
                setSelectModel(v);
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
        ObserverManager.getInstance().deleteObserver(this);
        BlueUtils.destroy();
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
            AppParams.sControl.setMode(Constant.Model.mode_default);
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


    @Override
    public void disConnected(BleDevice bleDevice) {
        setBlueData();
    }
}
