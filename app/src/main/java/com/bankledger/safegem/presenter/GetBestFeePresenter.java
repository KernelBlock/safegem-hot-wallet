package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.response.BestFeeResponse;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.view.IGetBestFeeView;

import java.util.List;

/**
 * @author bankledger
 * @time 2018/8/31 11:18
 */
public class GetBestFeePresenter extends BasePresenter<IGetBestFeeView> {

    private final Context mContext;

    public GetBestFeePresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void getBestFee() {
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.getBestFee()
                .compose(RxSchedulers.compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<List<BestFeeResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<BestFeeResponse> bestFees) {
                        if (getView() != null) {
                            if( bestFees != null) {
                                getView().onSuccess(bestFees);
                            }else{
                                getView().onError();
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        getView().onError();
                    }
                });
    }
}
