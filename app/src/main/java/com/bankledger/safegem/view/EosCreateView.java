package com.bankledger.safegem.view;

import com.bankledger.safegem.net.model.response.CheckEosAccountResponse;

/**
 * Created by zm on 2018/12/11.
 */

public interface EosCreateView extends IBaseLifecycleView {

    void onRegisterEosAccountComplete(String account, int opType);

    void onCheckEosAccountComplete(CheckEosAccountResponse response);

}
