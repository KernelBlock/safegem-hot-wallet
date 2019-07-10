package com.bankledger.safegem.ui.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.EthTokenUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.ActiveERC20Request;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.google.zxing.WriterException;

import java.math.BigDecimal;

import butterknife.BindView;

public class CurrencyActivity extends BaseActivity {

    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.address_tv)
    TextView address;
    @BindView(R.id.decimals)
    TextView decimals;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.code_img)
    ImageView codeImg;
    @BindView(R.id.tb)
    ToggleButton toggleButton;
    private ERCResponse.ERC ercResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        ercResponse = (ERCResponse.ERC) getIntent().getSerializableExtra(Constants.INTENT_DATA);
        setTitle(getString(R.string.coin_message));
        String ethAddress = SafeGemApplication.getInstance().getCurrentETHAddress();

        BigDecimal math = new BigDecimal(10);
        BigDecimal decimal = math.pow(Integer.parseInt(ercResponse.getDecimals()));
        total.setText(BigDecimalUtils.div(ercResponse.getTotalSupply(), decimal.toPlainString(), 4));

        address.setText(ercResponse.getContractAddress());
        decimals.setText(ercResponse.getDecimals());
        name.setText(ercResponse.getName());
        try {
            Bitmap content = QRCodeEncoderUtils.encodeAsBitmap(this, Utils.formatActiveEthToken(ethAddress, ercResponse));
            codeImg.setImageBitmap(content);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        toggleButton.setChecked(ercResponse.getActivication() == 1);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (!compoundButton.isPressed()) {
                    return;    //加这一条，否则当我setChecked()时会触发此listener
                }
                activeERC20Token(ercResponse, isCheck);
            }
        });
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra(Constants.INTENT_DATA,ercResponse);
                setResult(Constants.RESULT_CODE,intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent();
            intent.putExtra(Constants.INTENT_DATA,ercResponse);
            setResult(Constants.RESULT_CODE,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 激活，反激活erc
     */
    public void activeERC20Token(ERCResponse.ERC ercResponse, boolean isActive){
        if (SafeGemApplication.getInstance().getColdUniqueId().length() == 0){
            ToastUtil.showShort(this, getString(R.string.please_add_wallet));
            toggleButton.setChecked(false);
            return;
        } if (SafeGemApplication.getInstance().getCurrentETHAddress().length() == 0){
            ToastUtil.showShort(this, getString(R.string.please_eth_address));
            toggleButton.setChecked(false);
            return;
        }
        DialogUtil.showProgressDialog(this);
        String address = SafeGemApplication.getInstance().getCurrentETHAddress();
        RetrofitManager.getInstance().mNetService.activeERC20(new ActiveERC20Request(SafeGemApplication.getInstance().getColdUniqueId(), ercResponse.getContractAddress(), isActive ? "1":"0"))
                .compose(RxSchedulers.<BaseResponse<ERCResponse.ERC>>compose())
                .subscribe(new ResponseObserver<ERCResponse.ERC>() {
                    @Override
                    protected void onHandleSuccess(ERCResponse.ERC content) {
                            MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(),SafeGemApplication.getInstance().getColdUniqueId(),
                                    ercResponse.getName(),address,0,ercResponse.getContractAddress(),ercResponse.getSymbol(),ercResponse.getDecimals(),ercResponse.getTotalSupply(),2,ercResponse.getIcon());
                            if (isActive){
                                ercResponse.setActivication(1);
                                new MonitorAddressUtil(CurrencyActivity.this).insertMonitorAddress(monitorAddressTb);
                                ToastUtil.showShort(getActivity(), getString(R.string.has_add_asset_list));
                            }else {
                                ercResponse.setActivication(0);
                                new MonitorAddressUtil(CurrencyActivity.this).deleteERCToken(monitorAddressTb);
                                new EthTokenUtil(CurrencyActivity.this).deleteERCToken(monitorAddressTb.getAddress(),monitorAddressTb.getContractAddress());
                                ToastUtil.showShort(getActivity(), getString(R.string.has_remove_asset_list));
                            }
                    }
                });
    }

}
