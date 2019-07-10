package com.bankledger.safegem.view;

import com.bankledger.safegem.entity.TransactionDetailBean;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;

/**
 * @author bankledger
 * @time 2018/9/3 17:35
 */
public interface ITransactionDetailView extends IBaseLifecycleView {

    void onGetTransactionDetail(TransactionDetailBean tx);

    void onGetEosTransactionDetail(EosTxTb tx);

    void onGetUsdtTransactionDetail(UsdtTxTb tx);

    void onGetUsdtBtcTransactionDetail(TransactionDetailBean tx);
}
