package com.bankledger.safegem.view;

import com.bankledger.safegem.net.model.response.CountryCodeResponse;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public interface ICountryCodeView extends IBaseLifecycleView {

    void onGetCountryCode(List<CountryCodeResponse> list);

}
