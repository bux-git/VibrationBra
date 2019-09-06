package com.example.common.di.component;


import com.example.common.di.module.AppModule;
import com.example.vibrationbra.MyApplication;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @description：
 * @author：bux on 2018/4/2 16:43
 * @email: 471025316@qq.com
 */

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

   // ApiService getApiService();

    MyApplication getApplication();

    Gson getGson();

   // OkHttpClient getOkHttpClient();

  //  HttpProxyCacheServer getProxyCacheServer();

}
