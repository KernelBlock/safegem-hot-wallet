package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.ActiveERC20Request;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.scan.ScanActivity;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.ToastUtil;

import com.trello.rxlifecycle2.LifecycleTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddERCTokenActivity extends BaseActivity {

    @BindView(R.id.input_edit)
    EditText inputEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_erctoken);
        ButterKnife.bind(this);
        setTitle(getString(R.string.submit_new_token));
    }

    @OnClick({R.id.scan_img, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_img:
                verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(AddERCTokenActivity.this, ScanActivity.class);
                        intent.putExtra(Constants.SCAN_FLAG, Constants.ADD_ADDRESS_BOOK);
                        startActivityForResult(intent, Constants.REQUEST_CODE);
                    }

                    @Override
                    public void onDenied() {

                    }
                });
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        if (inputEdit.getText().toString().trim().length() == 0) {
            ToastUtil.showShort(this, R.string.please_input_address);
            return;
        }
        if (SafeGemApplication.getInstance().getColdUniqueId().length() == 0) {
            ToastUtil.showShort(this, R.string.please_add_wallet_tip);
            return;
        }
        if (SafeGemApplication.getInstance().getCurrentETHAddress().length() == 0) {
            ToastUtil.showShort(this, getString(R.string.please_eth_address));
            return;
        }
        DialogUtil.showProgressDialog(this);
        RetrofitManager.getInstance().mNetService.activeERC20(new ActiveERC20Request(SafeGemApplication.getInstance().getColdUniqueId(), inputEdit.getText().toString(), "1"))
                .compose(RxSchedulers.<BaseResponse<ERCResponse.ERC>>compose())
                .compose(bindLifecycle())
                .subscribe(new ResponseObserver<ERCResponse.ERC>() {
                    @Override
                    protected void onHandleSuccess(ERCResponse.ERC ercResponse) {
                        ercResponse.setActivication(1);
                        MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(), SafeGemApplication.getInstance().getColdUniqueId(),
                                ercResponse.getName(), SafeGemApplication.getInstance().getCurrentETHAddress(), 0, ercResponse.getContractAddress(), ercResponse.getSymbol(), ercResponse.getDecimals(), ercResponse.getTotalSupply(), 2, ercResponse.getIcon());
                        boolean flag = new MonitorAddressUtil(AddERCTokenActivity.this).insertMonitorAddress(monitorAddressTb);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.INTENT_DATA, ercResponse);
                        setResult(Constants.RESULT_CODE, intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.ADD_ADDRESS_BOOK:
                String address = data.getStringExtra(Constants.SCAN_DATA);
                inputEdit.setText(address);
                break;
        }
    }

    public LifecycleTransformer bindLifecycle() {
        return bindToLifecycle();
    }

}
