package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.presenter.SetPwdPresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.EditValidateUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.ISetPwdView;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends MVPBaseActivity<ISetPwdView, SetPwdPresenter> implements ISetPwdView {

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.pwd_edit)
    EditText pwdEdit;
    @BindView(R.id.country_tv)
    TextView countryTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_str);
        EditValidateUtil.getInstance().fiterTrim(phone);
        EditValidateUtil.getInstance().fiterTrim(pwdEdit);
    }

    @OnClick({R.id.country_tv, R.id.login_btn, R.id.register_tv, R.id.forget_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.country_tv:
                startActivityForResult(new Intent(LoginActivity.this, CountryCodeActivity.class), Constants.REQUEST_CODE);
                break;
            case R.id.login_btn:
                check();
                break;
            case R.id.register_tv:
                ActivitySkipUtil.skipAnotherActivityFinish(LoginActivity.this, RegisterCodeActivity.class);
                break;
            case R.id.forget_tv:
                ActivitySkipUtil.skipAnotherActivityFinish(LoginActivity.this, ForgetActivity.class);
                break;
        }
    }

    private void check() {
        if (phone.getText().toString() == null || phone.getText().toString().length() == 0) {
            ToastUtil.showShort(this, R.string.phone_tip1);
            return;
        }
        if (pwdEdit.getText().toString() == null || pwdEdit.getText().toString().length() == 0) {
            ToastUtil.showShort(this, R.string.pwd_tip);
            return;
        }
        if (countryTv.getText().toString().equals(Constants.CHINA_CODE) && !EditValidateUtil.getInstance().isMobileNO(phone.getText().toString())) {
            ToastUtil.showShort(this, R.string.phone_tip2);
            return;
        }
        mPresenter.login(Constants.LEFT_BRACKET + countryTv.getText().toString() + Constants.RIGHT_BRACKET + phone.getText().toString(), pwdEdit.getText().toString());
    }

    @Override
    protected SetPwdPresenter createPresenter() {
        return new SetPwdPresenter(this);
    }

    @Override
    public void onRegisterSuccess() {
        ActivitySkipUtil.skipAnotherActivityFinish(this, MainActivity.class);
    }

    @Override
    public void onModifyPasswordSuccess() {

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

}
