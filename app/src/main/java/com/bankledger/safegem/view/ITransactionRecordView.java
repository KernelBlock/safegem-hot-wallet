package com.bankledger.safegem.view;

import com.bankledger.safegem.entity.TransactionRecordBean;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;

import java.util.List;

/**
 * @author bankledger
 * @time 2018/9/1 14:11
 */
public interface ITransactionRecordView extends IBaseLifecycleView {

    void onGetTransactionRecord(List<TransactionRecordBean> recordBeans);

    void onGetEosTransactionRecord(List<EosTxTb> recordBeans);

    void onGetUsdtTransactionRecord(List<UsdtTxTb> recordBeans);

    void onTxLoadFinish();
}
