package com.bankledger.safegem.view;

import android.graphics.Bitmap;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public interface IRegCodeView extends IBaseLifecycleView {

    void phoneExist();

    void showImageCode(Bitmap bitmap);

    void onGetCode();

    void onValidateCodeSuccess(String content);

    void onError(String message);

}
