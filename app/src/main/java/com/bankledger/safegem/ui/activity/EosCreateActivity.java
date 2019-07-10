package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bankledger.protobuf.bean.TransEos;
import com.bankledger.safegem.R;
import com.bankledger.safegem.net.model.response.CheckEosAccountResponse;
import com.bankledger.safegem.presenter.EosCreatePresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.EosDigitsInputFilter;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.EosCreateView;

import butterknife.BindView;

/**
 * Created by zm on 2018/12/11.
 */
public class EosCreateActivity extends MVPBaseActivity<EosCreateView, EosCreatePresenter> implements EosCreateView {

    @BindView(R.id.reg_edit)
    EditText regEdit;

    @BindView(R.id.owner_key_edit)
    TextView ownerKeyEdit;

    @BindView(R.id.active_key_edit)
    TextView activeKeyEdit;

    private TransEos transEos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eos_create);
        setTitle(Constants.EOS);
        Intent intent = getIntent();
        transEos = (TransEos) intent.getSerializableExtra(Constants.INTENT_KEY1);
        setView(transEos.account, transEos.owner, transEos.active);

        findViewById(R.id.commit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(regEdit.getText())) {
                    ToastUtil.showShort(getActivity(), getString(R.string.eos_account_name_tips));
                    return;
                }
                if (regEdit.getText().length() != 12) {
                    ToastUtil.showShort(getActivity(), getString(R.string.eos_account_name_rule_tips));
                    return;
                }
                transEos.account = regEdit.getText().toString();
                //1：新建，2：导入
                switch (transEos.opType) {
                    case 1:
                        mPresenter.registerEosAccount(transEos);
                        break;
                    case 2:
                        mPresenter.checkEosAccount(transEos.account);
                        break;
                }
            }
        });
    }

    private void setView(String account, String owner, String active) {
        regEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12), new EosDigitsInputFilter()});
        regEdit.setText(account);
        regEdit.setSelection(regEdit.getText().length());
        ownerKeyEdit.setText(owner);
        activeKeyEdit.setText(active);
    }

    @Override
    protected EosCreatePresenter createPresenter() {
        return new EosCreatePresenter(this);
    }

    @Override
    public void onRegisterEosAccountComplete(String account, int opType) {
        switch (opType) {
            case 1:
                ToastUtil.showShort(this, getString(R.string.eos_create_success));
                break;
            case 2:
                ToastUtil.showShort(this, getString(R.string.eos_import_success));
                break;
        }
        ActivitySkipUtil.skipAnotherActivity(EosCreateActivity.this, EosAccountActivity.class);
        finish();
    }

    @Override
    public void onCheckEosAccountComplete(CheckEosAccountResponse response) {
        if (response.getAccount().equals(transEos.account)
                && response.getOwnerPubKey().equals(transEos.owner)
                && response.getActivePubKey().equals(transEos.active)) {
            mPresenter.addMonotior(transEos, transEos.account, 2);
        } else {
            ToastUtil.showShort(getActivity(), getString(R.string.eos_import_error));
        }
    }


}
