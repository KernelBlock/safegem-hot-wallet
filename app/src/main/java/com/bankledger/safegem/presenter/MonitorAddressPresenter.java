package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.protobuf.bean.Address;
import com.bankledger.protobuf.bean.TransMulAddress;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.EthTokenUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.AddColdMonitorAddressRequest;
import com.bankledger.safegem.net.model.response.AddColdMonitorAddressResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.EthUtil;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.IMonitorAddressView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class MonitorAddressPresenter extends BasePresenter<IMonitorAddressView> {

    private TxUtil txUtil;
    private EthTokenUtil ethTokenUtil;
    private MonitorAddressUtil monitorAddressUtil;
    private EosBalanceUtil eosUtil;
    private SafeAssetUtil safeAssetUtil;

    public MonitorAddressPresenter(Context mContext) {
        this.mContext = mContext;
        txUtil = new TxUtil(mContext);
        ethTokenUtil = new EthTokenUtil(mContext);
        monitorAddressUtil = new MonitorAddressUtil(mContext);
        eosUtil = new EosBalanceUtil(mContext);
        safeAssetUtil = new SafeAssetUtil(mContext);
    }

    private Context mContext;

    /**
     * 添加监控地址
     */
    public void addColdMonitorAddress(TransMulAddress transMulAddress) {
        long userId = SafeGemApplication.getInstance().getUserInfo().getUserId();
        if (!transMulAddress.walletSeqNumber.equals(SafeGemApplication.getInstance().getColdUniqueId())) {
            ToastUtil.showShort(mContext, mContext.getString(R.string.please_go_wallet_monitor));
            return;
        }
        List<AddColdMonitorAddressRequest.Monitor> monitorList = new ArrayList<>();
        List<MonitorAddressTb> monitorAddressTbList = new ArrayList<>();
        for (Address addressBean : transMulAddress.addressList) {
            if (monitorAddressUtil.isMinerMonitorAddress(userId, addressBean.address, addressBean.coinType)) {
                continue;
            }
            MonitorAddressTb monitorAddressTb = new MonitorAddressTb(userId, transMulAddress.walletSeqNumber, addressBean.coin, addressBean.address, 0);
            monitorAddressTbList.add(monitorAddressTb);
            monitorList.add(new AddColdMonitorAddressRequest.Monitor(addressBean.coin, "", addressBean.address));
            if (addressBean.coinType == Constants.COIN_USDT) { //USDT同步添加BTC到监控地址
                if (monitorAddressUtil.isMinerMonitorAddress(userId, addressBean.address, Constants.COIN_BTC)) {
                    continue;
                }
                MonitorAddressTb btcMonitorAddrTb = new MonitorAddressTb(userId, transMulAddress.walletSeqNumber, Constants.BTC, addressBean.address, Constants.COIN_BTC, 0);
                monitorAddressTbList.add(btcMonitorAddrTb);
                monitorList.add(new AddColdMonitorAddressRequest.Monitor(Constants.BTC, "", addressBean.address));
            }
        }

        if (monitorList.size() > 0) {
            DialogUtil.showProgressDialog(mContext);
            RetrofitManager.getInstance().mNetService.addColdMonitorAddress(new AddColdMonitorAddressRequest(transMulAddress.walletSeqNumber, monitorList))
                    .compose(getView().bindLifecycle())
                    .doOnNext(new Consumer<BaseResponse<AddColdMonitorAddressResponse>>() {
                        @Override
                        public void accept(BaseResponse<AddColdMonitorAddressResponse> response) throws Exception {
                            monitorAddressUtil.insertMonitorAddress(monitorAddressTbList);
                        }
                    })
                    .compose(RxSchedulers.<BaseResponse<AddColdMonitorAddressResponse>>compose())
                    .subscribe(new ResponseObserver<AddColdMonitorAddressResponse>() {
                        @Override
                        protected void onHandleSuccess(AddColdMonitorAddressResponse addColdMonitorAddressResponse) {
                            ToastUtil.showShort(mContext, R.string.add_success);
                            if (getView() != null) {
                                getView().onUpdateWalletComplete(true);
                            }
                        }
                    });
        } else {
            if (getView() != null) {
                getView().onUpdateWalletComplete(false);
            }
            ToastUtil.showShort(mContext, R.string.add_success);
        }
    }

    public void getColdMonitorAddress() {
        Observable
                .create(new ObservableOnSubscribe<List<String>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<String>> e) {
                        List<String> coinList = monitorAddressUtil.queryWalletCoinNoEthToken();
                        if (coinList != null && coinList.size() > 0) {
                            e.onNext(coinList);
                        }
                        e.onComplete();
                    }
                })
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<String> coinList) {
                        return Observable.fromIterable(coinList);
                    }
                })
                .map(new Function<String, MonitorAddressEntity>() {
                    @Override
                    public MonitorAddressEntity apply(String coin) {
                        MonitorAddressEntity monitorAddressEntity = new MonitorAddressEntity();
                        monitorAddressEntity.coin = coin;
                        monitorAddressEntity.name = coin;
                        monitorAddressEntity.monitorAddressList = monitorAddressUtil.queryUserCoinMonitorAddressTbs(coin);
                        if (coin.equals(Constants.ETH)) {
                            monitorAddressEntity.coinType = Constants.COIN_ETH;
                            monitorAddressEntity.ethAmount = EthUtil.formatETHAmount(ethTokenUtil.getAmountWithAddress(monitorAddressUtil.queryUserETHMonitorAddress(), ""));
                            monitorAddressEntity.count = 1;
                        } else if (coin.equals(Constants.ETC)) {
                            monitorAddressEntity.coinType = Constants.COIN_ETC;
                            monitorAddressEntity.ethAmount = EthUtil.formatETHAmount(ethTokenUtil.getAmountWithAddress(monitorAddressUtil.queryUserETCMonitorAddress(), ""));
                            monitorAddressEntity.count = 1;
                        } else if (coin.equals(Constants.EOS)) {
                            monitorAddressEntity.coinType = Constants.COIN_EOS;
                            monitorAddressEntity.ethAmount = StringUtil.subZeroAndDot(eosUtil.getAmountWithAddress(SafeGemApplication.getInstance().getCurrentEOSAccount(), coin));
                            monitorAddressEntity.count = 1;
                        } else if (coin.equals(Constants.USDT)) {
                            monitorAddressEntity.coinType = Constants.COIN_USDT;
                            String balance = eosUtil.getAmountWithUsdt(monitorAddressEntity.monitorAddressList.get(0).getAddress());
                            monitorAddressEntity.ethAmount = StringUtil.subZeroAndDot(balance);
                            monitorAddressEntity.count = 1;
                        } else {
                            monitorAddressEntity.coinType = Constants.COIN_BTC;
                            monitorAddressEntity.ethAmount = txUtil.getAmountWithCoin(coin, "");
                            monitorAddressEntity.count = monitorAddressEntity.monitorAddressList.size();
                        }
                        monitorAddressEntity.assetId = "";
                        return monitorAddressEntity;
                    }
                })
                .collect(new Callable<List<MonitorAddressEntity>>() {
                    @Override
                    public List<MonitorAddressEntity> call() {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<MonitorAddressEntity>, MonitorAddressEntity>() {
                    @Override
                    public void accept(List<MonitorAddressEntity> monitorAddressEntities, MonitorAddressEntity monitorAddressEntity) {
                        monitorAddressEntities.add(monitorAddressEntity);
                        if (monitorAddressEntity.coinType == Constants.COIN_ETH) { // 处理ERC20代币
                            List<MonitorAddressTb> tokenMonitorAddressTbs = monitorAddressUtil.queryUserCoinEthTokenMonitorAddressTb();
                            for (MonitorAddressTb item : tokenMonitorAddressTbs) {
                                String amount = EthUtil.formatETHTokenAmount(ethTokenUtil.getAmountWithAddress(SafeGemApplication.getInstance().getCurrentETHAddress(), item.getContractAddress()), Integer.parseInt(item.getDecimals()));
                                tokenMonitorAddressTbs = monitorAddressUtil.queryUserCoinEthTokenMonitorAddressTb(item.getContractAddress());
                                monitorAddressEntities.add(new MonitorAddressEntity(Constants.ETH, item.getSymbol(), 1, amount, Constants.COIN_ETH_TOKEN, item.getCoinImg(), tokenMonitorAddressTbs));
                            }
                        } else if (monitorAddressEntity.coinType == Constants.COIN_EOS) { // 处理EOS代币
                            List<EosBalanceTb> eosBalanceTbs = eosUtil.queryEosTokenBalanceTb(SafeGemApplication.getInstance().getCurrentEOSAccount());
                            for (EosBalanceTb eosBalanceTb : eosBalanceTbs) {
                                String amount = StringUtil.subZeroAndDot(eosBalanceTb.getBalance());
                                List<MonitorAddressTb> eosTokenList = new ArrayList<>();
                                MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(), SafeGemApplication.getInstance().getColdUniqueId(), eosBalanceTb.getTokenName(), eosBalanceTb.getAccount(), Constants.COIN_EOS_TOKEN, 0);
                                eosTokenList.add(monitorAddressTb);
                                monitorAddressEntities.add(new MonitorAddressEntity(Constants.EOS, eosBalanceTb.getTokenName(), 1, amount, Constants.COIN_EOS_TOKEN, "", eosTokenList));
                            }
                        } else if (monitorAddressEntity.name.equals(Constants.SAFE)) {  // 处理SAFE代币
                            List<SafeAssetTb> safeAssetTbs = safeAssetUtil.getSafeAssetList(monitorAddressEntity.name);
                            for (SafeAssetTb item : safeAssetTbs) {
                                String amount = StringUtil.subZeroAndDot(txUtil.getAmountWithCoin(monitorAddressEntity.name, item.getAssetId()));
                                monitorAddressEntities.add(new MonitorAddressEntity(Constants.SAFE, item.getAssetName(), monitorAddressEntity.count, amount, Constants.COIN_SAFE_ASSET, "", monitorAddressEntity.monitorAddressList, item.getAssetId()));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MonitorAddressEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<MonitorAddressEntity> recordBeans) {
                        if (getView() != null) {
                            getView().onGetMonitorAddressSuccess(recordBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }


}