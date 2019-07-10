package com.bankledger.safegem.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.UserUtil;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.UserTb;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.LanguageUtil;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.SharedPreferencesUtil;
import com.bankledger.safegem.utils.Utils;
//import com.tencent.bugly.crashreport.CrashReport;

import org.w3c.dom.Text;

import java.util.LinkedList;


/**
 * Created by zm on 2018/6/28.
 */

public class SafeGemApplication extends MultiDexApplication {

    // 登录用户信息
    private UserTb userTb;

    // 最后一次发送交易
    private String lastSendTx = "";

    // 此处采用 LinkedList作为容器，增删速度快
    public LinkedList<Activity> activityLinkedList;

    public SharedPreferencesUtil appSharedPrefUtil;

    private static SafeGemApplication instance;

    private TxUtil txUtil;
    private SafeAssetUtil safeAssetUtil;
    private MonitorAddressUtil monitorAddressUtil;


    public static SafeGemApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        txUtil = new TxUtil(this);
        safeAssetUtil = new SafeAssetUtil(this);
        monitorAddressUtil = new MonitorAddressUtil(this);
        appSharedPrefUtil = SharedPreferencesUtil.getDefaultPreferences(this);
        onLanguageChange();
        initActivityTask();
    }

    private void initActivityTask() {
        activityLinkedList = new LinkedList<>();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityLinkedList.add(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityLinkedList.remove(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

        });
    }

    public void exitApp() {
        for (Activity activity : activityLinkedList) {
            activity.finish();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void clearUserInfo() {
        this.userTb = null;
    }

    public UserTb getUserInfo() {
        if (this.userTb == null) {
            UserUtil utils = new UserUtil(this);
            this.userTb = utils.queryUserTb();
        }
        if (this.userTb != null && TextUtils.isEmpty(Constants.Companion.getAUTHORIZATION_HEADER())) {
            Constants.Companion.setAUTHORIZATION_HEADER(Utils.getBase64(this.userTb.getUserId() + ":" + this.userTb.getTokenId()));
        }
        return this.userTb;
    }

    public void setUser(UserTb userTb) {
        this.userTb = userTb;
    }

    public String getUserId() {
        UserTb userInfo = getUserInfo();
        if (userInfo != null) {
            return Long.toString(userInfo.getUserId());
        }
        return "";
    }

    public Long getUserIdToLong() {
        UserTb userInfo = getUserInfo();
        return userInfo.getUserId();
    }

    public boolean isUserNotNull() {
        if (getUserInfo() != null) {
            return true;
        }
        return false;
    }

    public String getColdUniqueId() {
        return appSharedPrefUtil.get(Constants.CURRENT_WALLET_ID, "");
    }

    public GetAppVersionResponse getColdVersionMessage() {
        if (TextUtils.isEmpty(appSharedPrefUtil.get(Constants.COLD_VERSION_MESSAGE, ""))) {
            return new GetAppVersionResponse(0, "", "", "", "", "");
        }
        return GsonUtils.getObjFromJSON(appSharedPrefUtil.get(Constants.COLD_VERSION_MESSAGE, ""), GetAppVersionResponse.class);
    }

    public String getCurrentETHAddress() {
        return monitorAddressUtil.queryUserETHMonitorAddress();
    }

    public String getCurrentEOSAccount() {
        return monitorAddressUtil.queryUserEOSMonitorAddress();
    }

    public String getCurrentUSDTAddress() {
        return monitorAddressUtil.queryUserUsdtMonitorAddress();
    }

    public SafeAssetTb getSafeAsset(String assetId) {
        return safeAssetUtil.querySafeAssetTb(assetId);
    }

    public void setLastMsgId(String msgId) {
        appSharedPrefUtil.put(getUserId(), msgId);
    }

    public String getLastMsgId() {
        return appSharedPrefUtil.get(getUserId(), "");
    }

    public String getLastSendTx() {
        return lastSendTx;
    }

    public void setLastSendTx(String lastSendTx) {
        this.lastSendTx = lastSendTx;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onLanguageChange();
    }

    public void onLanguageChange(){
        String language = SafeGemApplication.getInstance().appSharedPrefUtil.get(Constants.LANGUAGE, "");
        LanguageUtil.applyLanguage(this, language);
        Constants.Companion.setLANGUAGE_HEADER(language);
    }

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferencesUtil spUtil = SharedPreferencesUtil.getDefaultPreferences(base);
        String language = spUtil.get(Constants.LANGUAGE, "");
        super.attachBaseContext(LanguageUtil.attachBaseContext(base, language));
        MultiDex.install(this);
    }

    public String lastTxId(String address) {
        return txUtil.getLastTransationTxHash(address);
    }
}
