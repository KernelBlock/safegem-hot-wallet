package com.bankledger.safegem.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.utils.ActivitySkipUtil;

/**
 * Date：2018/8/16
 * Author: bankledger
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Integer time = 1000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (((SafeGemApplication) getApplication()).getUserInfo() != null) {
                    ActivitySkipUtil.skipAnotherActivityFinish(SplashActivity.this, MainActivity.class);
                } else {
                    ActivitySkipUtil.skipAnotherActivityFinish(SplashActivity.this, LoginRegGuideActivity.class);
                }
            }
        }, time);
    }
}
