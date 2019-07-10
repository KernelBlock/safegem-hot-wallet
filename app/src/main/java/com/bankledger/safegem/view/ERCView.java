package com.bankledger.safegem.view;

import com.bankledger.safegem.net.model.response.ERCResponse;

import java.util.List;

/**
 * Date：2018/9/13
 * Author: bankledger
 */
public interface ERCView extends IBaseLifecycleView{

    void onGetERCComplete(List<ERCResponse> content);

}
