package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.presenter.RegCodePresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.EditValidateUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.IRegCodeView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ForgetActivity extends MVPBaseActivity<IRegCodeView, RegCodePresenter> implements IRegCodeView {

    @BindView(R.id.country_tv)
    TextView countryTv;
    @BindView(R.id.phoneNum)
    EditText phoneNum;
    @BindView(R.id.red_tv)
    TextView redTv;
    @BindView(R.id.input_code)
    EditText inputCode;
    @BindView(R.id.get_code_tv)
    TextView getCodeTv;
    @BindView(R.id.time_tv)
    TextView time_tv;
    private final int count = Constants.SYMS_CODE_TIME;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        setTitle(R.string.forget_pwd_str);
        EditValidateUtil.getInstance().fiterTrim(phoneNum);
        EditValidateUtil.getInstance().fiterTrim(inputCode);
    }

    @Override
    protected RegCodePresenter createPresenter() {
        return new RegCodePresenter(this);
    }

    @OnClick({ R.id.confirm_btn, R.id.register_tv, R.id.get_code_tv, R.id.country_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.country_tv:
                startActivityForResult(new Intent(ForgetActivity.this, CountryCodeActivity.class), Constants.REQUEST_CODE);
                break;
            case R.id.register_tv:
                ActivitySkipUtil.skipAnotherActivityFinish(ForgetActivity.this, LoginActivity.class);
                break;
            case R.id.confirm_btn:
                check();
                break;
            case R.id.get_code_tv:
                if (phoneNum.getText().toString() == null || phoneNum.getText().toString().length() == 0) {
                    ToastUtil.showShort(this, R.string.phone_tip1);
                    return;
                }
                if (countryTv.getText().toString().equals(Constants.CHINA_CODE) && !EditValidateUtil.getInstance().isMobileNO(phoneNum.getText().toString())) {
                    ToastUtil.showShort(this, R.string.phone_tip2);
                    return;
                }
                mPresenter.getImageCode();
                break;
        }
    }

    private void check() {
        if (phoneNum.getText().toString() == null || phoneNum.getText().toString().length() == 0) {
            ToastUtil.showShort(this, R.string.phone_tip1);
            return;
        }
        if (inputCode.getText().toString() == null || inputCode.getText().toString().length() == 0) {
            ToastUtil.showShort(this, R.string.code_tip1);
            return;
        }
        if (countryTv.getText().toString().equals(Constants.CHINA_CODE) && !EditValidateUtil.getInstance().isMobileNO(phoneNum.getText().toString())) {
            ToastUtil.showShort(this, R.string.phone_tip2);
            return;
        }
        mPresenter.validateSmsCode(Constants.LEFT_BRACKET+countryTv.getText().toString().trim()+Constants.RIGHT_BRACKET + phoneNum.getText().toString().trim(),inputCode.getText().toString());
    }

    @Override
    public void phoneExist() {

    }

    @Override
    public void showImageCode(Bitmap bitmap) {
        DialogUtil.showImageDialog(this, bitmap, new DialogUtil.OnClickConfirmListener() {
            @Override
            public void onClickConfirm(String content) {
                mPresenter.sendSmsCode(Constants.LEFT_BRACKET+countryTv.getText().toString().trim()+Constants.RIGHT_BRACKET + phoneNum.getText().toString(), content);
            }
        });
    }

    @Override
    public void onGetCode() {
        redTv.setText(getString(R.string.has_send_str) + phoneNum.getText().toString() + getString(R.string.send_code_str));
        /**
         * 开始倒计时
         */
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        time_tv.setVisibility(View.VISIBLE);
                        getCodeTv.setVisibility(View.GONE);
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Long num) {
                time_tv.setText(num + getString(R.string.seconds_str));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                //回复原来初始状态
                time_tv.setVisibility(View.GONE);
                getCodeTv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onValidateCodeSuccess(String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.PHONE, Constants.LEFT_BRACKET+countryTv.getText().toString().trim()+Constants.RIGHT_BRACKET + phoneNum.getText().toString().trim());
        map.put(Constants.SMS_CODE, content);
        ActivitySkipUtil.skipAnotherActivity(ForgetActivity.this, ForgetSetPwdActivity.class, map);
    }

    @Override
    public void onError(String message) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            if (resultCode == Constants.RESULT_CODE) {
                countryTv.setText(data.getStringExtra(Constants.RESPONSE_DATA));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null){
            disposable.dispose();
        }
    }
}
