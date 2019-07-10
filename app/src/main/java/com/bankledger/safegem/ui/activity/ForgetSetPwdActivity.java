package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.bankledger.safegem.R;
import com.bankledger.safegem.presenter.SetPwdPresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.EditValidateUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.ISetPwdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetSetPwdActivity extends MVPBaseActivity<ISetPwdView, SetPwdPresenter> implements ISetPwdView {

    @BindView(R.id.new_pwd)
    EditText newPwd;
    @BindView(R.id.pwd_edit)
    EditText pwdEdit;
    private String phone, sms_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        ButterKnife.bind(this);
        setTitle(R.string.forget_pwd_str);
        EditValidateUtil.getInstance().fiterTrim(newPwd);
        EditValidateUtil.getInstance().fiterTrim(pwdEdit);
    }

    @Override
    protected SetPwdPresenter createPresenter() {
        return new SetPwdPresenter(this);
    }

    @Override
    public void initData() {
        super.initData();
        phone = getIntent().getStringExtra(Constants.PHONE);
        sms_code = getIntent().getStringExtra(Constants.SMS_CODE);

    }

    @OnClick(R.id.login_btn)
    public void onViewClicked() {
        if (newPwd.getText().toString().length() == 0 || pwdEdit.getText().toString().length() == 0) {
            ToastUtil.showShort(this, R.string.pwd_tip);
            return;
        }
        if (!newPwd.getText().toString().equals(pwdEdit.getText().toString())) {
            ToastUtil.showShort(this, R.string.confrim_pwd_tip);
            return;
        }
        mPresenter.modifyPassword(phone, sms_code, newPwd.getText().toString());
    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onModifyPasswordSuccess() {
        ActivitySkipUtil.skipAnotherActivityFinish(this, LoginActivity.class);
    }
}
