package cn.hbu.hebeiuniversity;

import android.app.Application;

import com.thinkland.sdk.android.JuheSDKInitializer;

/**
 * Created by Administrator on 2016/9/21/021.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JuheSDKInitializer.initialize(getApplicationContext());
    }
}
