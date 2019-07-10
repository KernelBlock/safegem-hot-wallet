package com.bankledger.safegem.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.UpdateBanlanceUtil;


import java.util.List;

import io.reactivex.ObservableSource;

/**
 * Author: bankledger
 * 轮询同步余额
 */
public class UpdateBalanceService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        loadWalletCoinList();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void loadWalletCoinList() {
        UpdateBanlanceUtil.getInstance().updateWalletBalance("", new OnUpdateBanlanceListener() {
            @Override
            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                Intent intent = new Intent(SafeGemApplication.getInstance().getPackageName() + Constants.RECEIVER);
                intent.putExtra(Constants.INTENT_DATA, Constants.SHOW_DATA);
                sendBroadcast(intent);
                return null;
            }
        });
    }

}

