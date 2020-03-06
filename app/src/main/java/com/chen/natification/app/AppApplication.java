package com.chen.natification.app;

/*
 * Author : Chen
 * Date : 2020/3/6
 * Time : 15:48
 * Email : Libertyed@163.com
 */

import android.app.Application;
import android.content.Context;

public class AppApplication extends Application {

    //项目APPlicaiton
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


}
