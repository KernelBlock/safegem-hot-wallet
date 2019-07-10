package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.ImgCodeRequest;
import com.bankledger.safegem.net.model.request.PhoneRequest;
import com.bankledger.safegem.net.model.request.SmsRequest;
import com.bankledger.safegem.net.model.request.ValidateSmsCodeRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ValidateSmsResponse;
import com.bankledger.safegem.utils.BitmapUtil;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.IRegCodeView;


/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class RegCodePresenter extends BasePresenter<IRegCodeView> {

    private Context mContext;
    private String random;

    public RegCodePresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 手机号码是否已注册
     */
    public void checkPhone(String phone) {
        PhoneRequest phoneRequest = new PhoneRequest();
        phoneRequest.phone = phone;
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.checkPhone(phoneRequest)
                .compose(RxSchedulers.<BaseResponse<String>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String content) {
                        if("1".equals(content)) {
                            getView().phoneExist();
                        } else {
                            getImageCode();
                        }
                    }
                });
    }

    /**
     * 获取图形验证码
     */
    public void getImageCode() {
        ImgCodeRequest imgCodeRequest = new ImgCodeRequest();
        imgCodeRequest.randomCode = Utils.getRandom();
        random = imgCodeRequest.randomCode;
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.getImgCode(imgCodeRequest)
                .compose(RxSchedulers.<BaseResponse<String>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String content) {
                        if (getView() != null && content != null) {
                            getView().showImageCode(BitmapUtil.base64ToBitmap(content));
                        }
                    }
                });
    }

    /**
     * 发送验证码
     */
    public void sendSmsCode(String phone,String imgCode){
        SmsRequest smsRequest=new SmsRequest(phone,imgCode,random);
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.sendSmsCode(smsRequest)
                .compose(RxSchedulers.<BaseResponse<String>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String content) {
                        if (getView() != null && content != null) {
                            getView().onGetCode();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        if (getView() != null){
                            getView().onError(msg);
                        }
                    }
                });
    }

    /**
     * 发送验证码
     */
    public void validateSmsCode(String phone,String sms){
        ValidateSmsCodeRequest validateSmsCodeRequest=new ValidateSmsCodeRequest(phone,sms);
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.validateSmsCode(validateSmsCodeRequest)
                .compose(RxSchedulers.<BaseResponse<ValidateSmsResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<ValidateSmsResponse>() {
                    @Override
                    protected void onHandleSuccess(ValidateSmsResponse content) {
                        if (getView() != null && content != null) {
                            getView().onValidateCodeSuccess(content.mobileAuthCode);
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        if (getView() != null){
                            getView().onError(msg);
                        }
                    }
                });
    }


}
