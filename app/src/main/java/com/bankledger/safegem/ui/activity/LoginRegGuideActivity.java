package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bankledger.safegem.R;
import com.bankledger.safegem.utils.ActivitySkipUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginRegGuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_reg_guide);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login_btn, R.id.register_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                ActivitySkipUtil.skipAnotherActivity(LoginRegGuideActivity.this, LoginActivity.class);
                break;
            case R.id.register_btn:
                ActivitySkipUtil.skipAnotherActivity(LoginRegGuideActivity.this, RegisterCodeActivity.class);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                safeGemApplication.exitApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
