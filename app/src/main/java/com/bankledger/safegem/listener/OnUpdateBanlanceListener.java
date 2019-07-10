package com.bankledger.safegem.listener;

import com.bankledger.protobuf.bean.CoinBalance;

import java.util.List;

import io.reactivex.ObservableSource;

/**
 * Date：2018/9/12
 * Author: bankledger
 */
public interface OnUpdateBanlanceListener {

    ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList);

}
