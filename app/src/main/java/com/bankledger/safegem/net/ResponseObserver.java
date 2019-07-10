package com.bankledger.safegem.net;

import android.support.annotation.NonNull;

import com.bankledger.safegem.R;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;


import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public abstract class ResponseObserver<T> implements Observer<BaseResponse<T>> {

    @Override
    public void onNext(BaseResponse<T> value) {
        DialogUtil.dismissProgressDialog();
        if (value.getError() != null) {
            onHandleError(value.getError());
        } else {
            if (value.getCode().equals("0")) {
                T t = value.getData();
                onHandleSuccess(t);
            } else {
                onHandleError(value.getMessage());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        onHandleError(e.toString());
        if (!Utils.isNetworkAvailable()) {
            ToastUtil.showShort(R.string.net_error);
        } else if (e instanceof SocketTimeoutException) {
            ToastUtil.showShort(R.string.request_error);
        } else if (e instanceof Thread.UncaughtExceptionHandler) {
            ToastUtil.showShort(R.string.request_error);
        }
        e.printStackTrace();
        DialogUtil.dismissProgressDialog();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
    }

}

