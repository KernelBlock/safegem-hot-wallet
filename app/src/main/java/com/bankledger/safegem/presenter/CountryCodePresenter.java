package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.ImgCodeRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.CountryCodeResponse;
import com.bankledger.safegem.utils.BitmapUtil;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ICountryCodeView;
import com.bankledger.safegem.view.IRegCodeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class CountryCodePresenter extends BasePresenter<ICountryCodeView> {

    private Context mContext;

    private List<CountryCodeResponse> allData = new ArrayList<>();

    public CountryCodePresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 短信国家代码
     */
    public void getCountryCode() {
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.getCountryCode()
                .compose(RxSchedulers.<BaseResponse<List<CountryCodeResponse>>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<List<CountryCodeResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<CountryCodeResponse> datas) {
                        if (getView() != null && datas != null) {
                            allData.addAll(datas);
                            getView().onGetCountryCode(datas);
                        }
                    }
                });
    }

    /**
     * 过滤国家码
     */
    public void filterCountryCode(String code) {
        code = code.toLowerCase();
        List<CountryCodeResponse> data = new ArrayList<>();
        for (CountryCodeResponse item : allData) {
            if (item.code.indexOf(code) >= 0 || item.zh.indexOf(code) >= 0 || item.en.toLowerCase().indexOf(code) >= 0) {
                data.add(item);
            }
        }
        if (getView() != null) {
            getView().onGetCountryCode(data);
        }
    }

}
