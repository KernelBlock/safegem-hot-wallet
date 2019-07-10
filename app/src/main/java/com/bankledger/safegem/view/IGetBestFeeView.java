package com.bankledger.safegem.view;

import com.bankledger.safegem.net.model.response.BestFeeResponse;

import java.util.List;

/**
 * @author bankledger
 * @time 2018/8/31 11:06
 */
public interface IGetBestFeeView extends IBaseLifecycleView {

    void onError();

    void onSuccess(List<BestFeeResponse> fees);
}
