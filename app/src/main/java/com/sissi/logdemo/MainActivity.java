package com.sissi.logdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String json = "{\"mtapi\":{\"head\":{\"eventid\":5154,\"eventname\":\"ImGetServerTimeRsp\",\"SessionID\": \"1\"},\"body\":{" +
            "   \"dwErrID\" : 0," +
            "   \"dwHandle\" : 3274818088" +
            "}" +
            "}" +
            "}";

    private String xml =
            "<android.support.constraint.ConstraintLayout>" +
            "</android.support.constraint.ConstraintLayout>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * 使用com.orhanobut.logger.Logger
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { // 动态检查及申请外部存储写权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        {
//        // 初始化。使用默认的android日志适配器
//        Logger.addLogAdapter(new AndroidLogAdapter()); // XXX: 放在这里并不合适，只需执行一次

            // 初始化。使用自定义android日志适配器
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                    .methodCount(0)         // (Optional) How many method line to show. Default 2
                    .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 0
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                    .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
                @Override
                public boolean isLoggable(int priority, @Nullable String tag) {
                    return priority>Logger.INFO || BuildConfig.DEBUG; // 可根据日志等级以及版本类型控制日志输出与否
                }
            }); // XXX: 放在这里并不合适，只需执行一次

            // 将日志输出一份到文件（文件路径是固定的，用户想更改需仿照DiskLogAdapter实现自己的adapter）
            // 保存的路径为Environment.getExternalStorageDirectory().getAbsolutePath/logger/logs_$count.csv NOTE：格式是csv
            Logger.addLogAdapter(new DiskLogAdapter());

            // 判断是否存在logger目录，若没有则创建。NOTE: 必须做此操作否则写日志失败且没任何提示。该库存在一些功能缺陷以及bug，此处即为其中之一。
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.pathSeparator+"logger");
            if (!file.exists()){
                if (!file.mkdirs()){
                    Logger.wtf("failed to create dir logger!");
                }
            }

            Logger.d(Environment.getExternalStorageDirectory().getAbsolutePath());
        }


        // 打印各个等级的日志
        Logger.d("formatted %s","debug"); // 支持格式化参数
        Logger.e("error");
        Logger.w("warning");
        Logger.v("verbose");
        Logger.i("information");
        Logger.wtf("What a Terrible Failure");

        // 打印集合
        Logger.d(Lists.newArrayList("a", "c", "b"));
        Logger.d(Lists.newArrayList("d", "e", "f").toArray());
        Logger.d(Sets.newHashSet(1, 2, 3, 5, 4));
        Logger.d(ImmutableMap.of(1, "a", 2, "b", 3, "c"));

        // 打印json
        Logger.json(json);  // 会对json字符串合法性进行校验

        // 打印xml
        Logger.xml(xml);  // 会对xml字符串合法性进行校验

        // 一次性打印超过4k的日志（android自带的Log会截断超过4k的部分）
        // NOTE: 但是Logger会在原来android自带的Log截断的位置换行然后再接着打印，这破坏了日志内容的完整性（无法分辨此处确实有个换行还是被Logger换行了）。
        StringBuffer sb = new StringBuffer();
        for (int i=1; i<=5*1024; ++i){
            if (i%1024==0){
                sb.append('\n');
            }
            sb.append(i/1024);
        }
        Logger.d(sb.toString());
        Log.i("AndroidLog", sb.toString());// 作为对比android自带的log只能打印4k（实际略小于4k）。


        /*使用com.elvishew.xlog.XLog*/
        XLog.init(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE);

        XLog.d("Simple message");
        XLog.d(Sets.newHashSet(1, 2, 3, 4));
        XLog.d(ImmutableMap.of(1, "a", 2, "b"));
        XLog.d(Lists.newArrayList("a", "b", "c"));
        XLog.json(json);
        XLog.xml(xml);
        XLog.d(sb.toString());

        // 局部用法
        com.elvishew.xlog.Logger partial = XLog.tag("partial").build();
        partial.d("partial msg");
        partial.d("partial msg 2");
    }

}
