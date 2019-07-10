package com.bankledger.safegem.view;

import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;

import java.util.List;

public interface ITransactionCoinView extends IBaseLifecycleView {

    void onGetMonitorAddr(List<CoinEntity> addressTbList);

}
