package com.example.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.vibrationbra.R;

import androidx.annotation.Nullable;

/**
 * @description：
 * @author：bux on 2019/9/4 11:49
 * @email: 471025316@qq.com
 */
public class SeekLayout extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "SeekLayout";

    TextView mTvExplain;
    ImageView mIvSub;
    ImageView mIvAdd;
    SeekBar mSeekBar;

    private int min = 1;
    private int max = 100;
    private String explainFormat = "时间:%1d分钟";

    /**
     * seekbar不能拖到最大值 当max值很小时，留出得空余进度值很大，所以，适当将进度增大以减小空余值
     */
    private float multiple = 10f;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (onProgressChangeLister != null) {
                onProgressChangeLister.onProgressChange(msg.arg1);
            }
        }
    };
    private OnProgressChangeLister onProgressChangeLister;

    public SeekLayout(Context context) {
        super(context);
    }

    public SeekLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    {
        inflate(getContext(), R.layout.seek_layout, this);
        mTvExplain = findViewById(R.id.tv_explain);
        mIvSub = findViewById(R.id.iv_sub);
        mIvAdd = findViewById(R.id.iv_add);
        mSeekBar = findViewById(R.id.seekBar);

        mIvAdd.setOnClickListener(this);
        mIvSub.setOnClickListener(this);

        initData();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d(TAG, "onProgressChanged: ");
                if (!fromUser) {
                    return;
                }
                setExplainText(progress, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.d(TAG, "onStopTrackingTouch: ");
                setProgressAndText(seekBar.getProgress());
            }
        });
    }

    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSeekBar.setMin(min);
        }
        mSeekBar.setMax((int) (max * multiple));
        setProgressAndText(mSeekBar.getProgress());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_sub:
                setProgressAndText(mSeekBar.getProgress() - multiple);
                break;
            case R.id.iv_add:
                setProgressAndText(mSeekBar.getProgress() + multiple);
                break;
            default:
        }

    }

    public void setProgress(int time) {
        setProgressAndText(time * multiple);
    }


    private void setProgressAndText(float v2) {
        setMathProgress((int) (v2));
        setExplainText((int) (v2), true);

    }


    private void setMathProgress(int progress) {
        //由于SEEK无法拖动到最大值 故做此处理
        //改变seekbar最大 最小区间
        int realProgress = getRealProgress(progress);
        if (realProgress == max) {
            progress = mSeekBar.getMax();
        } else if (realProgress == min) {
            progress = min;
        }
        mSeekBar.setProgress(progress);
    }

    /**
     * 设置进度显示文字
     *
     * @param progress
     */
    public void setExplainText(int progress, boolean isSend) {
        //显示处理之后得
        progress = getRealProgress(progress);
        if (progress < min) {
            progress = min;
        } else if (progress > max) {
            progress = max;
        }
        mTvExplain.setText(String.format(explainFormat, progress));
        if (!isSend) {
            return;
        }
        sendMessage(progress);
    }


    private int getRealProgress(int progress) {
        progress = (int) Math.ceil(progress / multiple);
        return progress;
    }

    /**
     * 设置数据
     *
     * @param min
     * @param max
     * @param explainFormat
     */
    public void setLimit(int min, int max, String explainFormat) {
        this.min = min;
        this.max = max;
        if (!TextUtils.isEmpty(explainFormat)) {
            this.explainFormat = explainFormat;
        }
        initData();
    }

    public void setOnProgressChangeLister(OnProgressChangeLister onProgressChangeLister) {
        this.onProgressChangeLister = onProgressChangeLister;
    }

    /**
     * 发送改变消息
     *
     * @param progress
     */
    private void sendMessage(int progress) {
        mHandler.removeMessages(1);
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.arg1 = progress;
        mHandler.sendMessageDelayed(message, 500);
    }


    public interface OnProgressChangeLister {
        void onProgressChange(int progress);
    }

}
