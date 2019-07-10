package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.view.ITransactionCoinView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TransactionCoinPresenter extends BasePresenter<ITransactionCoinView> {

    private MonitorAddressUtil monitorUtil;
    private EosBalanceUtil eosUtil;
    private SafeAssetUtil safeAssetUtil;

    public TransactionCoinPresenter(Context mContext) {
        monitorUtil = new MonitorAddressUtil(mContext);
        eosUtil = new EosBalanceUtil(mContext);
        safeAssetUtil = new SafeAssetUtil(mContext);
    }

    public void getTransactionCoin(String userId, String coldUniqueId) {
        Observable
                .create(new ObservableOnSubscribe<List<CoinEntity>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<CoinEntity>> e) throws Exception {
                        List<CoinEntity> coinList = new ArrayList<>();
                        List<MonitorAddressTb> addressList = monitorUtil.queryCurrentMonitorNoEth2Etc(userId, coldUniqueId);
                        for (MonitorAddressTb addressTb : addressList) {
                            CoinEntity parent = new CoinEntity();
                            parent.coin = addressTb.getCoin();
                            parent.coinType = addressTb.getCoinType();
                            coinList.add(parent);
                            if (addressTb.getCoinType() == Constants.COIN_EOS) { // 处理EOS代币
                                List<EosBalanceTb> eosBalanceTbs = eosUtil.queryEosTokenBalanceTb(SafeGemApplication.getInstance().getCurrentEOSAccount());
                                for (EosBalanceTb item : eosBalanceTbs) {
                                    CoinEntity child = new CoinEntity();
                                    child.coin = item.getTokenName();
                                    child.coinType = Constants.COIN_EOS_TOKEN;
                                    coinList.add(child);
                                }
                            } else if (addressTb.getCoinType() == Constants.COIN_ETH) { //处理ETH代币
                                List<MonitorAddressTb> tokenAddressTbs = monitorUtil.queryUserCoinEthTokenMonitorAddressTb();
                                for (MonitorAddressTb item : tokenAddressTbs) {
                                    CoinEntity child = new CoinEntity();
                                    child.coin = item.getSymbol();
                                    child.coinType = Constants.COIN_ETH_TOKEN;
                                    child.coinImg = item.getCoinImg();
                                    coinList.add(child);
                                }
                            } else if (addressTb.getCoin().equals(Constants.SAFE)) {  // 处理SAFE代币
                                List<SafeAssetTb> safeAssetTbs = safeAssetUtil.getSafeAssetList(addressTb.getCoin());
                                for (SafeAssetTb item : safeAssetTbs) {
                                    CoinEntity child = new CoinEntity();
                                    child.coin = Constants.SAFE;
                                    child.coinType = Constants.COIN_SAFE_ASSET;
                                    child.assetId = item.getAssetId();
                                    child.assetName = item.getAssetName();
                                    coinList.add(child);
                                }
                            }
                        }
                        e.onNext(coinList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CoinEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CoinEntity> coinList) {
                        getView().onGetMonitorAddr(coinList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
