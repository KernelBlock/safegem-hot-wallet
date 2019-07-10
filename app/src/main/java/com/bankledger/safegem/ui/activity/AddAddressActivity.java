package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.AddressBookUtil;
import com.bankledger.safegem.greendaodb.entity.AddressBookTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.AddAddressBookRequest;
import com.bankledger.safegem.net.model.response.AddColdMonitorAddressResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.scan.ScanActivity;
import com.bankledger.safegem.ui.widget.CommonEditWidget;
import com.bankledger.safegem.utils.AddressInputFilter;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.DigitsInputFilter;
import com.bankledger.safegem.utils.EditLengthInputFilter;
import com.bankledger.safegem.utils.EmojiFilter;
import com.bankledger.safegem.utils.EosDigitsInputFilter;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class AddAddressActivity extends BaseActivity {

    @BindView(R.id.cew_address)
    CommonEditWidget cewAddress;
    @BindView(R.id.cew_alias)
    CommonEditWidget cewAlias;
    @BindView(R.id.iv_currency_icon)
    ImageView ivCurrencyIcon;
    @BindView(R.id.tv_currency_name)
    TextView tvCurrencyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add_address));
        setRightText(getString(R.string.save_str), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivCurrencyIcon.setImageResource(R.drawable.btc_img);
        tvCurrencyName.setText(Constants.BTC);
        cewAddress.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(120), new AddressInputFilter()});
        cewAlias.getEditText().setFilters(new InputFilter[]{new EditLengthInputFilter(12)});
    }

    private void saveAddress() {
        String coin = tvCurrencyName.getText().toString();
        String address = cewAddress.getText();
        if (coin.equals(getString(R.string.select_coin))) {
            ToastUtil.showShort(this, getString(R.string.select_coin_tip));
            return;
        }
        if (address.trim().length() == 0) {
            ToastUtil.showShort(this, getString(R.string.select_address_tip));
            return;
        }
        if (EmojiFilter.containsEmoji(cewAlias.getText().toString())){
            ToastUtil.showShort(this, getString(R.string.cannot_emoji));
            return;
        }
        AddAddressBookRequest addAddressBookRequest = new AddAddressBookRequest();
        addAddressBookRequest.addressList.add(new AddAddressBookRequest.AddressBook(coin, cewAlias.getText(), address));
        DialogUtil.showProgressDialog(this);
        RetrofitManager.getInstance().mNetService.addAddressbook(addAddressBookRequest)
                .compose(RxSchedulers.<BaseResponse<AddColdMonitorAddressResponse>>compose())
                .compose(bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<AddColdMonitorAddressResponse>>() {
                    @Override
                    public void accept(BaseResponse<AddColdMonitorAddressResponse> response) throws Exception {
                        AddressBookTb addressBookTb = new AddressBookTb(SafeGemApplication.getInstance().getUserInfo().getUserId(), coin, cewAlias.getText(), address);
                        new AddressBookUtil(AddAddressActivity.this).insertAddressBook(addressBookTb);
                    }
                })
                .subscribe(new ResponseObserver<AddColdMonitorAddressResponse>() {
                    @Override
                    protected void onHandleSuccess(AddColdMonitorAddressResponse content) {
                        if (content.getFailed().size() == 0) {
                            ToastUtil.showShort(getActivity(), getString(R.string.add_success));
                            finish();
                        } else {
                            ToastUtil.showShort(getActivity(), R.string.add_repeat);
                        }
                    }
                });

    }

    public LifecycleTransformer bindLifecycle() {
        return bindToLifecycle();
    }

    @Override
    public void initView() {
        super.initView();
        cewAddress.setHint(R.string.wallet_address);
        cewAddress.setRightImageResource(R.drawable.scan_qr);
        cewAddress.getRightImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(AddAddressActivity.this, ScanActivity.class);
                        intent.putExtra(Constants.SCAN_FLAG, Constants.ADD_ADDRESS_BOOK);
                        startActivityForResult(intent, Constants.REQUEST_CODE);
                    }

                    @Override
                    public void onDenied() {
                    }
                });
            }
        });
        cewAlias.setHint(R.string.remark);
    }

    @OnClick(R.id.ll_currency)
    public void onViewClicked() {
        Intent intent = new Intent(AddAddressActivity.this, SelectCoinActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.RESULT_CODE:
                String coin = data.getStringExtra(Constants.INTENT_DATA);
                if (coin != null) {
                    ivCurrencyIcon.setImageResource(Utils.getCoinImg(coin, Utils.coin2Type(coin)));
                    tvCurrencyName.setText(StringUtil.getDisplayName(coin));
                    if (Utils.coin2Type(coin) == Constants.COIN_EOS) {
                        cewAddress.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(12), new EosDigitsInputFilter()});
                    } else {
                        cewAddress.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(120), new AddressInputFilter()});
                    }
                }
                break;
            case Constants.ADD_ADDRESS_BOOK:
                String address = data.getStringExtra(Constants.SCAN_DATA);
                if (address != null) {
                    String[] arr = address.split(QRCodeUtil.QR_CODE_COLON);
                    if (arr.length == 1) {
                        cewAddress.setText(address);
                    } else if (arr.length == 2) {
                        coin = arr[0].toUpperCase();
                        List<String> coinList = Arrays.asList(Constants.Companion.getCOIN_ARR());
                        if (coinList.contains(coin)) {
                            tvCurrencyName.setText(StringUtil.getDisplayName(coin));
                            ivCurrencyIcon.setImageResource(Utils.getCoinImg(coin, Utils.coin2Type(coin)));
                            cewAddress.setText(getAddressText(arr[1]));
                        } else {
                            cewAddress.setText(getAddressText(address));
                        }
                    } else {
                        cewAddress.setText(null);
                    }
                }
                break;
        }
    }

    public String getAddressText(String address) {
        if (address != null) {
            if (address.contains("?amount")) {
                address = address.substring(0, address.indexOf("?amount"));
            }
        }
        return address;
    }

}
