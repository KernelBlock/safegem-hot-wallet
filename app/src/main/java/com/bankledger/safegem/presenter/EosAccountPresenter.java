package com.bankledger.safegem.presenter;


import android.content.Context;

import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.EosAccountRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.EosAccountResponse;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.view.EosAccountView;

public class EosAccountPresenter extends BasePresenter<EosAccountView> {

    private Context mContext;

    public EosAccountPresenter(Context context) {
        this.mContext = context;
    }

    /**
     * 获取账户
     * @param account
     */
    public void getEosAccount(String account) {
        DialogUtil.showProgressDialog(mContext);
        EosAccountRequest eosAccountRequest = new EosAccountRequest(account);
        RetrofitManager.getInstance().mNetService.getEosAccount(eosAccountRequest)
                .compose(RxSchedulers.<BaseResponse<EosAccountResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<EosAccountResponse>() {
                    @Override
                    protected void onHandleSuccess(EosAccountResponse datas) {
                        if (getView() != null) {
                            getView().onGetEosAccount(datas);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().onError(account);
                        }
                    }
                });
    }

}
