package com.bankledger.safegem.presenter;

import android.content.Context;
import android.util.Log;

import com.bankledger.safegem.entity.TransactionDetailBean;
import com.bankledger.safegem.greendaodb.EosTxUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.UsdtTxUtil;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.view.ITransactionDetailView;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @author bankledger
 * @time 2018/9/3 17:38
 */
public class TransactionDetailPresenter extends BasePresenter<ITransactionDetailView> {

    private final TxUtil txUtil;
    private final EosTxUtil eosTxTbUtil;
    private final UsdtTxUtil usdtTxTbUtil;

    public TransactionDetailPresenter(Context context) {
        txUtil = new TxUtil(context);
        eosTxTbUtil = new EosTxUtil(context);
        usdtTxTbUtil = new UsdtTxUtil(context);
    }

    public void getTransactionDetail(String txHash, String coldUniqueId) {
        Observable
                .just(txHash)
                .map(new Function<String, TransactionDetailBean>() {
                    @Override
                    public TransactionDetailBean apply(String txHash) throws Exception {
                        TxTb tx = txUtil.queryTxWithTxHash(txHash);
                        boolean isSend = txUtil.txIsSend(txHash, coldUniqueId);
                        String txAmount = txUtil.getTxAmount(txHash, coldUniqueId, isSend);
                        String txAddress = txUtil.getTxAddress(txHash, coldUniqueId, isSend);
                        String minerFee = isSend ? txUtil.getMineFee(txHash) : "0";
                        return new TransactionDetailBean(tx, txAmount, txAddress, minerFee, isSend);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TransactionDetailBean>() {
                    @Override
                    public void accept(TransactionDetailBean transactionDetailBean) throws Exception {
                        if (getView() != null){
                            getView().onGetTransactionDetail(transactionDetailBean);
                        }
                    }
                });
    }

    public void getEosTransactionDetail(String txHash) {
        Observable
                .just(txHash)
                .map(new Function<String, EosTxTb>() {
                    @Override
                    public EosTxTb apply(String s) throws Exception {
                        EosTxTb tx = eosTxTbUtil.queryEosTxTbForTxId(s);
                        return tx;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EosTxTb>() {
                    @Override
                    public void accept(EosTxTb tx) throws Exception {
                        if (getView() != null){
                            getView().onGetEosTransactionDetail(tx);
                        }
                    }
                });
    }

    public void getUsdtTransactionDetail(String txHash) {
        Observable
                .just(txHash)
                .map(new Function<String, UsdtTxTb>() {
                    @Override
                    public UsdtTxTb apply(String s) throws Exception {
                        UsdtTxTb tx = usdtTxTbUtil.queryTxWithTxHash(s);
                        return tx;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UsdtTxTb>() {
                    @Override
                    public void accept(UsdtTxTb tx) throws Exception {
                        if (getView() != null){
                            getView().onGetUsdtTransactionDetail(tx);
                        }
                    }
                });
    }

    public void getUsdtBtcTransactionDetail(String txHash, String coldUniqueId) {
        Observable
                .just(txHash)
                .map(new Function<String, TransactionDetailBean>() {
                    @Override
                    public TransactionDetailBean apply(String s) throws Exception {
                        TxTb tx = txUtil.queryTxWithTxHash(s);
                        if (tx != null) {
                            boolean isSend = txUtil.txIsSend(s, coldUniqueId);
                            String txAmount = txUtil.getTxAmount(s, coldUniqueId, isSend);
                            String txAddress = txUtil.getTxAddress(s, coldUniqueId, isSend);
                            String minerFee = isSend ? txUtil.getMineFee(txHash) : "0";
                            return new TransactionDetailBean(tx, txAmount, txAddress, minerFee, isSend);
                        } else {
                            return new TransactionDetailBean();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TransactionDetailBean>() {
                    @Override
                    public void accept(TransactionDetailBean tx) throws Exception {
                        if (getView() != null){
                            getView().onGetUsdtBtcTransactionDetail(tx);
                        }
                    }
                });
    }
}
