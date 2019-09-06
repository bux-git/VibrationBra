package com.example.common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.example.vibrationbra.MyApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @description：
 * @author：bux on 2018/4/3 10:51
 * @email: 471025316@qq.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int START_TIME = 1;
    Unbinder mUnbinder;
    MyApplication mApplication;
    public Context mContext;

    public static void start(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(setLayoutId());
        mUnbinder = ButterKnife.bind(this);
        mApplication = (MyApplication) getApplication();
        init();

    }

    /**
     * 设置布局ID
     *
     * @return
     */
    protected abstract int setLayoutId();


    /**
     * 初始化操作
     */
    protected abstract void init();


    @Override
    protected void onDestroy() {
        if (mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }


    protected void showMsg(String msg) {
        ToastUtils.showShort(msg);
    }


}
