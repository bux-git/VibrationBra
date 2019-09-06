package com.example.vibrationbra;

import android.app.Application;
import android.content.Context;

import com.example.common.di.component.AppComponent;
import com.example.common.di.component.DaggerAppComponent;
import com.example.common.di.module.AppModule;
import com.google.gson.Gson;

import androidx.multidex.MultiDex;

/**
 * @description：APP启动
 * @author：bux on 2019/9/3 16:28
 * @email: 471025316@qq.com
 */
public class MyApplication extends Application {

    private static MyApplication mMyApplication;
    //蓝牙是否链接
    public static boolean BLE_CONNECT = false;
    AppComponent mAppComponent;


    public AppComponent getAppComponent() {
        if (mAppComponent == null) {
            mAppComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return mAppComponent;
    }

    public static MyApplication get() {
        return mMyApplication;
    }

    public static Gson getGson() {
        return mMyApplication.getAppComponent().getGson();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
