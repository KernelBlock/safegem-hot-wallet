package com.bankledger.safegem.presenter;


import android.content.Context;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.GetERCListRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.view.ERCView;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/13
 * Author: bankledger
 */
public class ERCPresenter extends BasePresenter<ERCView> {

    private Context mContext;

    public ERCPresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取erc
     */
    public void getERC20Token() {
        DialogUtil.showProgressDialog(mContext);
        GetERCListRequest getERCListRequest = new GetERCListRequest();
        getERCListRequest.coldUniqueIdList.add(SafeGemApplication.getInstance().getColdUniqueId());
        RetrofitManager.getInstance().mNetService.getERC20List(getERCListRequest)
                .compose(getView().bindLifecycle())
                .compose(RxSchedulers.<BaseResponse<List<ERCResponse>>>compose())
                .subscribe(new ResponseObserver<List<ERCResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<ERCResponse> content) {
                        if (getView() != null) {
                            getView().onGetERCComplete(content);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().onGetERCComplete(new ArrayList<>());
                        }
                    }

                });
    }


    /**
     * 搜索
     */
    public void searchERC20Token(String keyWord) {
        GetERCListRequest getERCListRequest = new GetERCListRequest();
        getERCListRequest.keyWord = keyWord;
        getERCListRequest.coldUniqueIdList.add(SafeGemApplication.getInstance().getColdUniqueId());
        RetrofitManager.getInstance().mNetService.getERC20List(getERCListRequest)
                .compose(RxSchedulers.<BaseResponse<List<ERCResponse>>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<List<ERCResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<ERCResponse> content) {
                        if (getView() != null) {
                            getView().onGetERCComplete(content);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().onGetERCComplete(new ArrayList<>());
                        }
                    }

                });
    }


}


