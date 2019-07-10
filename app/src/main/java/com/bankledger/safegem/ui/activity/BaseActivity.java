package com.bankledger.safegem.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.LanguageUtil;
import com.bankledger.safegem.utils.StatusBarUtils;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by zm on 2018/6/22.
 */

public abstract class BaseActivity extends RxAppCompatActivity {


    private static final int VERIFY_CODE = 0x01; //申请权限代码
    private  PermissionCallBack callBack;
    private Unbinder bind;

    private Toolbar toolbar;
    private TextView tvTitle,rightTv;
    private View right_view;
    private ImageView rightImg;
    protected SafeGemApplication safeGemApplication;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        bind = ButterKnife.bind(this);

        /**
         * Toolbar初始化
         */
        toolbar = findViewById(R.id.toolbar);
        if (null != toolbar){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle = findViewById(R.id.tv_title);
            rightTv=findViewById(R.id.tv_right);
            rightImg=findViewById(R.id.img_right);
            right_view=findViewById(R.id.right_view);
            setDefaultNavigation();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtils.setStatusBarColor(this,getColor(R.color.title_bar_bg));
        }else {
            StatusBarUtils.setStatusBarColor(this,getResources().getColor(R.color.title_bar_bg));
        }
        safeGemApplication = (SafeGemApplication) getApplication();

        initView();
        initData();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);            //竖屏
    }

    public void initView(){

    }

    public void initData(){

    }


    /**
     * 申请权限
     *
     * @param reqestPermissions
     * @param callBack
     */
    public void verifyPermissions(String reqestPermissions, PermissionCallBack callBack) {
        this.callBack = callBack;
        int checkPermission = ActivityCompat.checkSelfPermission(this, reqestPermissions);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    BaseActivity.this,
                    new String[]{reqestPermissions},
                    VERIFY_CODE
            );
        } else {
            callBack.onGranted();
        }
    }

    public void verifyPermissions(String[] reqestPermissions, PermissionCallBack callBack) {
        this.callBack = callBack;
        int checkPermission = ActivityCompat.checkSelfPermission(this, reqestPermissions[1]);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    BaseActivity.this,
                    reqestPermissions,
                    VERIFY_CODE
            );
        } else {
            callBack.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.VERIFY_CODE == requestCode) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                callBack.onGranted();
            } else {
                // Permission Denied
                callBack.onDenied();
            }
        }
    }

    public interface PermissionCallBack {
        void onGranted();
        void onDenied();
    }

    public BaseActivity getActivity(){
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitle(String title, View.OnClickListener listener) {
        tvTitle.setText(title);
        tvTitle.setOnClickListener(listener);
    }

    public void setTitle(int titleId) {
        tvTitle.setText(titleId);
    }

    public void setTitle(int titleId, View.OnClickListener listener) {
        tvTitle.setText(titleId);
        tvTitle.setOnClickListener(listener);
    }

    protected void setDefaultNavigation() {
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_30dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setLeftClickListener(View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_30dp);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    public void setNavigation(int iconId, View.OnClickListener onNavigationClickListener) {
        toolbar.setNavigationIcon(iconId);
        toolbar.setNavigationOnClickListener(onNavigationClickListener);
    }

    public void setRightText(String text, View.OnClickListener onClickListener) {
        rightTv.setVisibility(View.VISIBLE);
        rightImg.setVisibility(View.GONE);
        rightTv.setText(text);
        rightTv.setOnClickListener(onClickListener);
    }

    public void setRightImage(int src, View.OnClickListener onClickListener) {
        rightTv.setVisibility(View.GONE);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setBackgroundResource(src);
        right_view.setOnClickListener(onClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = SafeGemApplication.getInstance().appSharedPrefUtil.get(Constants.LANGUAGE, "");
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

}
