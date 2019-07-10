package com.bankledger.safegem.view;

import com.bankledger.safegem.net.model.response.EosAccountResponse;

public interface EosAccountView extends IBaseLifecycleView {

    void onGetEosAccount(EosAccountResponse eosAccountResponse);

    void onError(String account);

}
