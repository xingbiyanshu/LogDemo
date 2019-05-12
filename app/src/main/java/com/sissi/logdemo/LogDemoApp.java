package com.sissi.logdemo;

import android.app.Application;
import android.os.Environment;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

public class LogDemoApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        String logPath = SDCARD + "/marssample/log";

        // this is necessary, or may cash for SIGBUS
        String cachePath = getFilesDir() + "/xlog";

        //init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 1, "");
            Xlog.setConsoleLogOpen(true);

        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 1, "");
            Xlog.setConsoleLogOpen(false);
        }

        Log.setLogImp(new Xlog());
        Log.i("TEST", "##############dddd");

//        Log.appenderClose(); // TODO app退出时做，但是怎么感知app退出呢？
    }

}
