package com.bankledger.safegem.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.EosAccountUtil;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.EthTokenUtil;
import com.bankledger.safegem.greendaodb.MessageUtils;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.EthTokenTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.TxOutsTb;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.CheckEosAccountRequest;
import com.bankledger.safegem.net.model.request.GetAssetIdRequest;
import com.bankledger.safegem.net.model.request.GetEosBalanceRequest;
import com.bankledger.safegem.net.model.request.GetEtcAddressInfoRequest;
import com.bankledger.safegem.net.model.request.GetEthAddressInfoRequest;
import com.bankledger.safegem.net.model.request.GetUsdtBalanceRequest;
import com.bankledger.safegem.net.model.request.TransactionListRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.CheckEosAccountResponse;
import com.bankledger.safegem.net.model.response.EOSBanlance;
import com.bankledger.safegem.net.model.response.GetAssetIdResponse;
import com.bankledger.safegem.net.model.response.GetEthAddressResponse;
import com.bankledger.safegem.net.model.response.TransactionResponse;
import com.bankledger.safegem.net.model.response.UsdtBanlanceResponse;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Date：2018/9/12
 * Author: bankledger
 * 同步余额工具类
 */
public class UpdateBanlanceUtil {

    private static volatile UpdateBanlanceUtil INSTANCE;
    private TxUtil txUtil = new TxUtil(SafeGemApplication.getInstance());
    private MessageUtils messageUtils = new MessageUtils(SafeGemApplication.getInstance());
    private EthTokenUtil ethTokenUtil = new EthTokenUtil(SafeGemApplication.getInstance());
    private MonitorAddressUtil monitorAddressUtil = new MonitorAddressUtil(SafeGemApplication.getInstance());
    private EosBalanceUtil eosTbUtil = new EosBalanceUtil(SafeGemApplication.getInstance());
    private SafeAssetUtil safeAssetUtil = new SafeAssetUtil(SafeGemApplication.getInstance());
    private EosAccountUtil eosAccountUtil = new EosAccountUtil(SafeGemApplication.getInstance());

    public static void clearInstance() {
        INSTANCE = null;
    }

    public static UpdateBanlanceUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (UpdateBanlanceUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UpdateBanlanceUtil();
                }
            }
        }
        return INSTANCE;
    }

    public void merageTransactionList(String coldUnqId, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        List<String> coinList = new ArrayList<>();
        if (coldUnqId.trim().length() == 0) {
            coinList.addAll(monitorAddressUtil.queryWalletCoin(Constants.COIN_BTC));
        } else {
            coinList.addAll(monitorAddressUtil.queryWalletCoin(Constants.COIN_BTC, coldUnqId));
        }
        if (coinList.size() == 0) {
            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            return;
        }
        String coin = coinList.remove(0);
        listLoadTransactionList(coinList, coin, onUpdateBanlanceListener);

    }

    public void listLoadTransactionList(List<String> coinList, String coin, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        List<String> addressList = new ArrayList<>();
        addressList.addAll(monitorAddressUtil.queryUserCoinMonitorAddress(coin));
        merageTransactionList(Utils.getAddressMessageList(addressList), coin, new OnUpdateBanlanceListener() {
            @Override
            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                if (coinList != null && coinList.size() > 0) {
                    String coin = coinList.remove(0);
                    listLoadTransactionList(coinList, coin, onUpdateBanlanceListener);
                } else {
                    if (onUpdateBanlanceListener != null) {
                        List<TxOutsTb> txOutsTbs = txUtil.getUnspendOuts();
                        onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createBTCUpdateBanlanceContent(txOutsTbs));
                    }
                }
                return null;
            }
        });
    }

    public void merageTransactionList(List<TransactionListRequest.AddressMessage> addressList, String coin, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        TransactionListRequest transactionListRequest = new TransactionListRequest(coin, Constants.REST_COUNT, addressList);
        RetrofitManager.getInstance().mNetService.getTransactionList(transactionListRequest)
                .doOnNext(new Consumer<BaseResponse<List<TransactionResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<TransactionResponse>> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            List<TransactionResponse> responseList = response.getData();
                            for (TransactionResponse transaction : responseList) {
                                if (transaction.txList.size() > 0) {
                                    txUtil.insertTx(transaction.txList, transaction.coin);
                                    long userId = SafeGemApplication.getInstance().getUserIdToLong();
                                    if (monitorAddressUtil.isMinerMonitorAddress(userId, transaction.address, Utils.coin2Type(transaction.coin))) { //监控地址
                                        messageUtils.insertTxMessage(transaction.coin, monitorAddressUtil.queryAddressWalletId(transaction.address), transaction.txList);
                                    }
                                }
                            }
                        }
                    }
                }).compose(RxSchedulers.<BaseResponse<List<TransactionResponse>>>compose())
                .subscribe(new ResponseObserver<List<TransactionResponse>>() {

                    @Override
                    protected void onHandleSuccess(List<TransactionResponse> transactionResponses) {
                        List<TransactionListRequest.AddressMessage> newAddressList = new ArrayList<>();
                        for (TransactionResponse transaction : transactionResponses) {
                            if (transaction.restCount > 0) {
                                int position = transaction.txList.size() - 1;
                                newAddressList.add(new TransactionListRequest.AddressMessage(transaction.address, transaction.txList.get(position).txid));
                            }
                        }
                        if (newAddressList.size() > 0) { //继续请求币种
                            merageTransactionList(newAddressList, coin, onUpdateBanlanceListener);
                        } else { //币已经加载完成
                            coinLoadFinish(coin, onUpdateBanlanceListener);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<>());
                        }
                    }

                });
    }

    public void coinLoadFinish(String coin, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        Observable.create(new ObservableOnSubscribe<List<TxOutsTb>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TxOutsTb>> e) throws Exception {
                txUtil.updateTxOuts();
                List<TxOutsTb> txOutsTbs = new ArrayList<>();
                txOutsTbs.addAll(txUtil.queryCoinOuts(coin));
                e.onNext(txOutsTbs);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<TxOutsTb>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<TxOutsTb> txOutsTbs) {
                if (coin.equals(Constants.SAFE)) {
                    List<String> assetIds = txUtil.getNoSyncSafeAsset(Constants.SAFE);
                    if (assetIds.size() > 0) {
                        updateSafeAssetId(assetIds, CreateCodeContentUtils.createBTCUpdateBanlanceContent(txOutsTbs), onUpdateBanlanceListener);
                    } else {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createBTCUpdateBanlanceContent(txOutsTbs));
                        }
                    }
                } else {
                    if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                        onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createBTCUpdateBanlanceContent(txOutsTbs));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                    onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onComplete() {
                setMsgBroadcast();
            }
        });
    }

    public void updateSafeAssetId(List<String> assetIds, List<CoinBalance> balanceList, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        GetAssetIdRequest getAssetIdRequest = new GetAssetIdRequest(assetIds);
        RetrofitManager.getInstance().mNetService.getAssetInfoList(getAssetIdRequest)
                .doOnNext(new Consumer<BaseResponse<List<GetAssetIdResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<GetAssetIdResponse>> response) throws Exception {
                        List<SafeAssetTb> safeAssetList = new ArrayList<>();
                        for (GetAssetIdResponse item : response.getData()) {
                            SafeAssetTb safeAssetTb = new SafeAssetTb();
                            safeAssetTb.setAssetId(item.assetId);
                            safeAssetTb.setAssetShortName(item.assetShortName);
                            safeAssetTb.setAssetName(item.assetName);
                            safeAssetTb.setAssetDesc(item.assetDesc);
                            safeAssetTb.setAssetUnit(item.assetUnit);
                            safeAssetTb.setAssetDecimals(item.assetDecimals);
                            safeAssetTb.setIssueTime(item.issueTime);
                            safeAssetList.add(safeAssetTb);
                        }
                        safeAssetUtil.insertSafeAsset(safeAssetList);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(balanceList);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<List<GetAssetIdResponse>>>compose())
                .subscribe(new ResponseObserver<List<GetAssetIdResponse>>() {

                    @Override
                    protected void onHandleSuccess(List<GetAssetIdResponse> getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(balanceList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(balanceList);
                        }
                    }

                });
    }

    public void updateWalletBalance(String coldUnqId, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        merageTransactionList(coldUnqId, new OnUpdateBanlanceListener() {
            @Override
            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> btcBalanceList) {
                if (!SafeGemApplication.getInstance().isUserNotNull()) {
                    return null;
                }
                String ethAddress = monitorAddressUtil.queryUserETHMonitorAddress();
                getEthAddressInfo(ethAddress, new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> ethBalanceList) {
                        List<String> ethMonitorList = monitorAddressUtil.queryUserEthTokenMonitorAddress();
                        getEthTokenAddressInfo(ethAddress, ethMonitorList, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> ethTokenBalanceList) {
                                String etcAddress = monitorAddressUtil.queryUserETCMonitorAddress();
                                getEtcAddressInfo(etcAddress, new OnUpdateBanlanceListener() {
                                    @Override
                                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> etcBalanceList) {
                                        String account = monitorAddressUtil.queryUserEOSMonitorAddress();
                                        getEosAddressInfo(account, new OnUpdateBanlanceListener() {
                                            @Override
                                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> eosBalanceList) {
                                                String address = monitorAddressUtil.queryUserUsdtMonitorAddress();
                                                getUsdtAddressInfo(address, new OnUpdateBanlanceListener() {
                                                    @Override
                                                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> usdtBalanceList) {
                                                        List<CoinBalance> coinBalances = new ArrayList<>();
                                                        coinBalances.addAll(btcBalanceList);
                                                        coinBalances.addAll(ethBalanceList);
                                                        coinBalances.addAll(ethTokenBalanceList);
                                                        coinBalances.addAll(etcBalanceList);
                                                        coinBalances.addAll(eosBalanceList);
                                                        coinBalances.addAll(usdtBalanceList);
                                                        onUpdateBanlanceListener.onUpdateSuccess(coinBalances);
                                                        return null;
                                                    }
                                                });
                                                return null;
                                            }
                                        });
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        return null;
                    }
                });
                return null;
            }
        });
    }


    /**
     * 获取ETH余额同步
     */
    public void getEthAddressInfo(String address, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        if (TextUtils.isEmpty(address)) {
            if (onUpdateBanlanceListener != null) {
                onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            }
            return;
        }
        GetEthAddressInfoRequest getEthAddressInfoRequest = new GetEthAddressInfoRequest();
        getEthAddressInfoRequest.address = address;
        getEthAddressInfoRequest.contractAddressList.add("");
        RetrofitManager.getInstance().mNetService.getEthAddressInfo(getEthAddressInfoRequest)
                .doOnNext(new Consumer<BaseResponse<GetEthAddressResponse>>() {
                    @Override
                    public void accept(BaseResponse<GetEthAddressResponse> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            GetEthAddressResponse getEthAddressResponse = response.getData();
                            if (monitorAddressUtil.isMinerMonitorAddress(address, "")) {
                                EthTokenTb ethTokenTb = new EthTokenTb(address, "", getEthAddressResponse.list.get(0).balance, getEthAddressResponse.transactionCount);
                                ethTokenUtil.addOrUpdateEth(ethTokenTb);
                            }
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<GetEthAddressResponse>>compose())
                .subscribe(new ResponseObserver<GetEthAddressResponse>() {
                    @Override
                    protected void onHandleSuccess(GetEthAddressResponse getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createETHUpdateBanlanceContent(address, getEthAddressResponse));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }

    /**
     * 获取EthToken余额同步
     */
    public void getEthTokenAddressInfo(String address, List<String> contractAddressList, OnUpdateBanlanceListener
            onUpdateBanlanceListener) {
        if (contractAddressList.size() == 0 || TextUtils.isEmpty(address)) {
            if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            }
            return;
        }
        GetEthAddressInfoRequest getEthAddressInfoRequest = new GetEthAddressInfoRequest();
        getEthAddressInfoRequest.address = address;
        getEthAddressInfoRequest.contractAddressList.addAll(contractAddressList);
        RetrofitManager.getInstance().mNetService.getEthAddressInfo(getEthAddressInfoRequest)
                .doOnNext(new Consumer<BaseResponse<GetEthAddressResponse>>() {
                    @Override
                    public void accept(BaseResponse<GetEthAddressResponse> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            GetEthAddressResponse getEthAddressResponse = response.getData();
                            for (GetEthAddressResponse.EthAddress ethAddress : getEthAddressResponse.list) {
                                if (monitorAddressUtil.isMinerMonitorAddress(address, ethAddress.contractAddress)) {
                                    EthTokenTb ethTokenTb = new EthTokenTb(address, ethAddress.contractAddress, ethAddress.balance, getEthAddressResponse.transactionCount);
                                    ethTokenUtil.addOrUpdateEth(ethTokenTb);
                                }
                            }
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<GetEthAddressResponse>>compose())
                .subscribe(new ResponseObserver<GetEthAddressResponse>() {

                    @Override
                    protected void onHandleSuccess(GetEthAddressResponse getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createETHTokenUpdateBanlanceContent(address, getEthAddressResponse));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }


    /**
     * 获取ETC余额同步
     */
    public void getEtcAddressInfo(String address, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        if (address == null || address.length() == 0) {
            if (onUpdateBanlanceListener != null) {
                onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            }
            return;
        }
        GetEtcAddressInfoRequest getEtcAddressInfoRequest = new GetEtcAddressInfoRequest();
        getEtcAddressInfoRequest.address = address;
        RetrofitManager.getInstance().mNetService.getEtcAddressInfo(getEtcAddressInfoRequest)
                .doOnNext(new Consumer<BaseResponse<GetEthAddressResponse>>() {
                    @Override
                    public void accept(BaseResponse<GetEthAddressResponse> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            GetEthAddressResponse getEthAddressResponse = response.getData();
                            EthTokenTb ethTokenTb = new EthTokenTb(address, "", getEthAddressResponse.balance, getEthAddressResponse.transactionCount);
                            ethTokenUtil.addOrUpdateEth(ethTokenTb);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<GetEthAddressResponse>>compose())
                .subscribe(new ResponseObserver<GetEthAddressResponse>() {
                    @Override
                    protected void onHandleSuccess(GetEthAddressResponse getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createETCUpdateBanlanceContent(address, getEthAddressResponse));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }

    /**
     * 获取Eos余额同步
     */
    public void getEosAddressInfo(String account, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        if (account == null || account.length() == 0) {
            if (onUpdateBanlanceListener != null) {
                onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            }
            return;
        }
        GetEosBalanceRequest getEosBalanceRequest = new GetEosBalanceRequest(account);
        RetrofitManager.getInstance().mNetService.getEosBalance(getEosBalanceRequest)
                .doOnNext(new Consumer<BaseResponse<List<EOSBanlance>>>() {
                    @Override
                    public void accept(BaseResponse<List<EOSBanlance>> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            List<EOSBanlance> eosBanlanceList = response.getData();
                            List<EosBalanceTb> eosBalanceTbs = new ArrayList<>();
                            for (EOSBanlance eosBanlance : eosBanlanceList) {
                                EosBalanceTb eosBalanceTb = new EosBalanceTb(account, eosBanlance.getBalance(), eosBanlance.getTokenName(), EosBalanceTb.TYPE_EOS);
                                eosBalanceTbs.add(eosBalanceTb);
                            }
                            if (eosBalanceTbs.size() > 0) {
                                eosTbUtil.insertEosBalanceTb(eosBalanceTbs);
                            }
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<List<EOSBanlance>>>compose())
                .subscribe(new ResponseObserver<List<EOSBanlance>>() {
                    @Override
                    protected void onHandleSuccess(List<EOSBanlance> getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            String coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
                            String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
                            EosAccountTb eosAccountTb = eosAccountUtil.queryCurrentEosAccountTb(coldUniqueId, account);
                            if (eosAccountTb != null) {
                                onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createEOSUpdateBanlanceContent(eosAccountTb, eosTbUtil.queryEosBalanceTb(account)));
                            } else {
                                checkEosAccount(coldUniqueId, account, onUpdateBanlanceListener);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }

    /**
     * 检查EOS账户
     *
     * @param account
     */
    private void checkEosAccount(String coldUniqueId, String account, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        CheckEosAccountRequest checkEosAccountRequest = new CheckEosAccountRequest(account);
        RetrofitManager.getInstance().mNetService.checkEosAccount(checkEosAccountRequest)
                .compose(RxSchedulers.<BaseResponse<CheckEosAccountResponse>>compose())
                .subscribe(new ResponseObserver<CheckEosAccountResponse>() {

                    @Override
                    protected void onHandleSuccess(CheckEosAccountResponse datas) {
                        EosAccountTb eosAccountTb = new EosAccountTb();
                        eosAccountTb.setColdUniqueId(coldUniqueId);
                        eosAccountTb.setAccount(account);
                        eosAccountTb.setActivePubKey(datas.getActivePubKey());
                        eosAccountTb.setOwnerPubKey(datas.getOwnerPubKey());
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createEOSUpdateBanlanceContent(eosAccountTb, eosTbUtil.queryEosBalanceTb(account)));
                        }
                        eosAccountUtil.insertEosAccountTb(eosAccountTb);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }

    /**
     * 获取Usdt余额同步
     */
    public void getUsdtAddressInfo(String address, OnUpdateBanlanceListener onUpdateBanlanceListener) {
        if (address == null || address.length() == 0) {
            if (onUpdateBanlanceListener != null) {
                onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
            }
            return;
        }
        GetUsdtBalanceRequest usdtBalanceRequest = new GetUsdtBalanceRequest(Constants.USDT, address);
        RetrofitManager.getInstance().mNetService.getUsdtBalance(usdtBalanceRequest)
                .doOnNext(new Consumer<BaseResponse<UsdtBanlanceResponse>>() {
                    @Override
                    public void accept(BaseResponse<UsdtBanlanceResponse> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            UsdtBanlanceResponse usdtResponse = response.getData();
                            EosBalanceTb eosBalanceTb = new EosBalanceTb(address, usdtResponse.getBalance(), Constants.USDT, EosBalanceTb.TYPE_USDT);
                            eosTbUtil.insertEosBalanceTb(eosBalanceTb);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<UsdtBanlanceResponse>>compose())
                .subscribe(new ResponseObserver<UsdtBanlanceResponse>() {

                    @Override
                    protected void onHandleSuccess(UsdtBanlanceResponse getEthAddressResponse) {
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(CreateCodeContentUtils.createUsdtUpdateBanlanceContent(SafeGemApplication.getInstance().getCurrentUSDTAddress(), eosTbUtil.queryEosBalanceTb(address)));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (onUpdateBanlanceListener != null && SafeGemApplication.getInstance().isUserNotNull()) {
                            onUpdateBanlanceListener.onUpdateSuccess(new ArrayList<CoinBalance>());
                        }
                    }

                });
    }

    public void setMsgBroadcast() {
        if (!SafeGemApplication.getInstance().getLastMsgId().equals(messageUtils.queryLastMsgId())) {
            Intent intent = new Intent(SafeGemApplication.getInstance().getPackageName() + Constants.RECEIVER);
            intent.putExtra(Constants.INTENT_DATA, Constants.NEW_COIN);
            SafeGemApplication.getInstance().sendBroadcast(intent);
        }
    }

}
