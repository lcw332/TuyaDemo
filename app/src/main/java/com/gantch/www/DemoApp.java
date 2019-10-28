package com.gantch.www;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.TuyaSdk;
import com.tuya.smart.sdk.api.INeedLoginListener;

public class DemoApp extends Application {

    private static final String TAG = "TuyaSmartApp";
    private static Context context;

    public static Context getAppContext() {
        return context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context =this;
        Log.d(TAG, "onCreate: "+ getProcessName(this));
        TuyaHomeSdk.init(this);//初始化
//        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
//            @Override
//            public void onNeedLogin(Context context) {
//                Intent intent = new Intent(context,MainActivity.class);
//                if (!(context instanceof Activity)) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                startActivity(intent);
//            }
//        });
        TuyaHomeSdk.setDebugMode(true);//开启debug模式
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

}
