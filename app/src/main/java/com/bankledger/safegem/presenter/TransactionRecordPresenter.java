package com.bankledger.safegem.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.TransactionRecordBean;
import com.bankledger.safegem.greendaodb.EosDelegateUtil;
import com.bankledger.safegem.greendaodb.EosRamUtil;
import com.bankledger.safegem.greendaodb.EosTxUtil;
import com.bankledger.safegem.greendaodb.MessageUtils;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.UsdtTxUtil;
import com.bankledger.safegem.greendaodb.entity.EosDelegateTb;
import com.bankledger.safegem.greendaodb.entity.EosRamTb;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.EosTxRequest;
import com.bankledger.safegem.net.model.request.TransactionListRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.EosTxResponse;
import com.bankledger.safegem.net.model.response.UsdtTransactionResponse;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ITransactionRecordView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * @author bankledger
 * @time 2018/9/1 14:13
 */
public class TransactionRecordPresenter extends BasePresenter<ITransactionRecordView> {

    private final TxUtil txUtil;
    private final EosTxUtil eosTxUtil;
    private final EosRamUtil eosRamUtil;
    private final EosDelegateUtil eosDelegateUtil;
    private final UsdtTxUtil usdtTxUtil;
    private final MessageUtils msgUtil;

    public TransactionRecordPresenter(Context mContext) {
        txUtil = new TxUtil(mContext);
        eosTxUtil = new EosTxUtil(mContext);
        eosRamUtil = new EosRamUtil(mContext);
        eosDelegateUtil = new EosDelegateUtil(mContext);
        usdtTxUtil = new UsdtTxUtil(mContext);
        msgUtil = new MessageUtils(mContext);
    }

    public void getBtcLocalTransaction(String coin, String assetId, int pageIndex) {
        pageIndex--;
        int offset = pageIndex * Constants.PAGE_SIZE;
        Observable.create(new ObservableOnSubscribe<List<TxTb>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TxTb>> e) {
                List<TxTb> txs = txUtil.queryMonitorTx(coin, assetId, offset, Constants.PAGE_SIZE);
                e.onNext(txs);
                e.onComplete();
            }
        })
                .flatMap(new Function<List<TxTb>, ObservableSource<TxTb>>() {
                    @Override
                    public ObservableSource<TxTb> apply(List<TxTb> txTbs) {
                        return Observable.fromIterable(txTbs);
                    }
                })
                .map(new Function<TxTb, TransactionRecordBean>() {
                    @Override
                    public TransactionRecordBean apply(TxTb txTb) {
                        TransactionRecordBean recordBeans = new TransactionRecordBean();
                        recordBeans.txHash = txTb.getTxHash();
                        recordBeans.coin = txTb.getCoin();
                        if (TextUtils.isEmpty(assetId)) {
                            recordBeans.coinType = Utils.coin2Type(txTb.getCoin());
                        } else {
                            recordBeans.coinType = Constants.COIN_SAFE_ASSET;
                        }
                        boolean isSend = txUtil.txIsSend(txTb.getTxHash(), SafeGemApplication.getInstance().getColdUniqueId());
                        recordBeans.amount = txUtil.getTxAmount(txTb.getTxHash(), SafeGemApplication.getInstance().getColdUniqueId(), isSend);
                        String address = txUtil.getTxAddress(txTb.getTxHash(), SafeGemApplication.getInstance().getColdUniqueId(), isSend);
                        recordBeans.address = address;
                        recordBeans.date = DateTimeUtil.getShortDateTimeString0(txTb.getTime() * 1000);
                        recordBeans.time = DateTimeUtil.getMontnDayHM(txTb.getTime() * 1000);
                        return recordBeans;
                    }
                })
                .collect(new Callable<List<TransactionRecordBean>>() {
                    @Override
                    public List<TransactionRecordBean> call() {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<TransactionRecordBean>, TransactionRecordBean>() {
                    @Override
                    public void accept(List<TransactionRecordBean> recordBeans, TransactionRecordBean transactionRecordBean) {
                        recordBeans.add(transactionRecordBean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TransactionRecordBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<TransactionRecordBean> recordBeans) {
                        if (getView() != null && recordBeans != null) {
                            getView().onGetTransactionRecord(recordBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void getUsdtLocalTransactionRecord(int pageIndex) {
        pageIndex--;
        int offset = pageIndex * Constants.PAGE_SIZE;
        String coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        List<UsdtTxTb> usdtTxList = usdtTxUtil.queryUsdtTxTb(coldUniqueId, offset, Constants.PAGE_SIZE);
        getView().onGetUsdtTransactionRecord(usdtTxList);
    }

    public void getUsdtTransactionRecord(String address) {
        String coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        String lastTxId = usdtTxUtil.getLastTransationTx(SafeGemApplication.getInstance().getColdUniqueId());
        List<TransactionListRequest.AddressMessage> addressList = new ArrayList<>();
        TransactionListRequest.AddressMessage addressMessage = new TransactionListRequest.AddressMessage(address, lastTxId);
        addressList.add(addressMessage);
        TransactionListRequest transactionListRequest = new TransactionListRequest(Constants.USDT, Constants.REST_COUNT, addressList);
        RetrofitManager.getInstance().mNetService.getUsdtTx(transactionListRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<List<UsdtTransactionResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<UsdtTransactionResponse>> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            List<UsdtTransactionResponse> mList = response.getData();
                            UsdtTransactionResponse usdtResponse = mList.get(0);
                            List<UsdtTxTb> usdtTxTbList = new ArrayList<>();
                            for (JsonObject item : usdtResponse.txList) {
                                UsdtTxTb txTb = new UsdtTxTb();
                                txTb.setCold_unique_id(coldUniqueId);
                                txTb.setAmount(item.get("amount").getAsString());
                                txTb.setBlock(item.get("block").getAsString());
                                txTb.setBlockhash(item.get("blockhash").getAsString());
                                txTb.setBlocktime(item.get("blocktime").getAsLong());
                                txTb.setConfirmations(item.get("confirmations").getAsString());
                                txTb.setFee(item.get("fee").getAsString());
                                txTb.setPositioninblock(item.get("positioninblock").getAsString());
                                txTb.setPropertyid(item.get("propertyid").getAsString());
                                txTb.setReferenceaddress(item.get("referenceaddress").getAsString());
                                txTb.setSendingaddress(item.get("sendingaddress").getAsString());
                                txTb.setTxid(item.get("txid").getAsString());
                                txTb.setVersion(item.get("version").getAsString());
                                usdtTxTbList.add(txTb);
                            }
                            usdtTxUtil.insertUsdtTxTb(usdtTxTbList);
                            msgUtil.insertUsdtMessage(Constants.USDT, usdtTxTbList);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<List<UsdtTransactionResponse>>>compose())
                .subscribe(new ResponseObserver<List<UsdtTransactionResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<UsdtTransactionResponse> obj) {
                        UsdtTransactionResponse usdtResponse = obj.get(0);
                        if (usdtResponse.restCount > 0) {
                            getUsdtTransactionRecord(address);
                        } else {
                            setMsgBroadcast();
                            getView().onTxLoadFinish();
                        }
                    }
                });
    }

    public void getEosLocalTransactionRecord(String account, String tokenName, int pageIndex) {
        pageIndex--;
        int offset = pageIndex * Constants.PAGE_SIZE;
        List<EosTxTb> eosTxList;
        if (TextUtils.isEmpty(tokenName)) {
            eosTxList = eosTxUtil.queryEosTxTb(account, offset, Constants.PAGE_SIZE);
        } else {
            eosTxList = eosTxUtil.queryEosTxTb(account, tokenName, offset, Constants.PAGE_SIZE);
        }
        getView().onGetEosTransactionRecord(eosTxList);
    }

    public void getEosTransactionRecord(String account, long position) {
        EosTxRequest eosTxRequest = new EosTxRequest(account, String.valueOf(position), String.valueOf(Constants.EOS_TX_COUNT));
        RetrofitManager.getInstance().mNetService.getEosTx(eosTxRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<EosTxResponse>>() {
                    @Override
                    public void accept(BaseResponse<EosTxResponse> response) throws Exception {
                        if (SafeGemApplication.getInstance().isUserNotNull()) {
                            EosTxResponse eosTxResponse = response.getData();
                            List<EosTxTb> eosTxList = new ArrayList<>();
                            List<EosRamTb> eosRamList = new ArrayList<>();
                            List<EosDelegateTb> eosDelegateList = new ArrayList<>();
                            List<JsonObject> jsonList = eosTxResponse.getActions();
                            if (jsonList.size() == 0) {
                                eosTxUtil.updateEosTx();
                                return;
                            }
                            for (JsonObject item : jsonList) {
                                JsonObject actionTraceObj = item.getAsJsonObject("action_trace");
                                String txId = actionTraceObj.get("trx_id").getAsString();
                                String height = actionTraceObj.get("block_num").getAsString();
                                String time = actionTraceObj.get("block_time").getAsString();
                                JsonObject actObj = actionTraceObj.getAsJsonObject("act");
                                String name = actObj.get("name").getAsString();
                                if (name.equals("transfer")) {
                                    JsonObject dataObj = actObj.get("data").getAsJsonObject();
                                    String from = dataObj.get("from").getAsString();
                                    String quantity = dataObj.get("quantity").getAsString();
                                    String memo = dataObj.get("memo").getAsString();
                                    String to = dataObj.get("to").getAsString();
                                    String[] arr = quantity.split(" ");
                                    EosTxTb txItem = new EosTxTb();
                                    txItem.setAccount(account);
                                    txItem.setTxId(txId);
                                    txItem.setHeight(height);
                                    txItem.setTime(time);
                                    txItem.setAmount(arr[0]);
                                    txItem.setCoin(arr[1]);
                                    txItem.setFrom(from);
                                    txItem.setMemo(memo);
                                    txItem.setTo(to);
                                    eosTxList.add(txItem);
                                } else if (name.equals("buyrambytes")) {
                                    JsonArray inlineTracesArr = actionTraceObj.get("inline_traces").getAsJsonArray();
                                    if (inlineTracesArr.size() > 0) {
                                        JsonObject dataObj = actObj.get("data").getAsJsonObject();
                                        JsonObject arrObj = inlineTracesArr.get(0).getAsJsonObject();
                                        JsonObject inlineTracesActObj = arrObj.getAsJsonObject("act");
                                        JsonObject inlineTracesDataObj = inlineTracesActObj.get("data").getAsJsonObject();
                                        String bytes = dataObj.get("bytes").getAsString();
                                        String from = inlineTracesDataObj.get("from").getAsString();
                                        String quantity = inlineTracesDataObj.get("quantity").getAsString();
                                        String memo = inlineTracesDataObj.get("memo").getAsString();
                                        String to = inlineTracesDataObj.get("to").getAsString();
                                        EosRamTb ramTb = new EosRamTb();
                                        ramTb.setTxId(txId);
                                        ramTb.setBytes(bytes);
                                        ramTb.setFrom(from);
                                        ramTb.setQuantity(quantity);
                                        ramTb.setTo(to);
                                        ramTb.setMemo(memo);
                                        eosRamList.add(ramTb);
                                    }
                                } else if (name.equals("delegatebw")) {
                                    JsonObject dataObj = actObj.get("data").getAsJsonObject();
                                    String from = dataObj.get("from").getAsString();
                                    String receiver = dataObj.get("receiver").getAsString();
                                    String stakeCpuQuantity = dataObj.get("stake_cpu_quantity").getAsString();
                                    String stakeNetQuantity = dataObj.get("stake_net_quantity").getAsString();
                                    String transfer = dataObj.get("transfer").getAsString();
                                    EosDelegateTb delegateTb = new EosDelegateTb();
                                    delegateTb.setTxId(txId);
                                    delegateTb.setFrom(from);
                                    delegateTb.setReceiver(receiver);
                                    delegateTb.setStakeCpuQuantity(stakeCpuQuantity);
                                    delegateTb.setStakeNetQuantity(stakeNetQuantity);
                                    delegateTb.setTransfer(transfer);
                                    eosDelegateList.add(delegateTb);
                                }
                            }
                            eosTxUtil.insertEosTxTb(eosTxList);
                            eosRamUtil.insertEosRamTb(eosRamList);
                            eosDelegateUtil.insertEosDelegateTb(eosDelegateList);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<EosTxResponse>>compose())
                .subscribe(new ResponseObserver<EosTxResponse>() {
                    @Override
                    protected void onHandleSuccess(EosTxResponse obj) {
                        List<JsonObject> jsonList = obj.getActions();
                        if (jsonList.size() > 0) {
                            JsonObject lastObj = jsonList.get(jsonList.size() - 1);
                            long postion = lastObj.get("account_action_seq").getAsLong();
                            getEosTransactionRecord(account, postion + 1);
                        } else {
                            setMsgBroadcast();
                            getView().onTxLoadFinish();
                        }
                    }
                });
    }

    public void setMsgBroadcast() {
        if (!SafeGemApplication.getInstance().getLastMsgId().equals(msgUtil.queryLastMsgId())) {
            Intent intent = new Intent(SafeGemApplication.getInstance().getPackageName() + Constants.RECEIVER);
            intent.putExtra(Constants.INTENT_DATA, Constants.NEW_COIN);
            SafeGemApplication.getInstance().sendBroadcast(intent);
        }
    }
}
