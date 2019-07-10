package com.bankledger.safegem.view;

import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public interface IMonitorAddressView extends IBaseLifecycleView{

    void onGetMonitorAddressSuccess(List<MonitorAddressEntity> monitorAddressBeans );

    void onUpdateWalletComplete(boolean isAdd);

}
