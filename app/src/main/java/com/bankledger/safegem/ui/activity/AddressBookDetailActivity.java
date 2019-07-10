package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.greendaodb.AddressBookUtil;
import com.bankledger.safegem.greendaodb.entity.AddressBookTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.DeleteAddressBookRequest;
import com.bankledger.safegem.net.model.request.UpdateAddressBookNameRequest;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.utils.ConfigUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.EditLengthInputFilter;
import com.bankledger.safegem.utils.EmojiFilter;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.google.zxing.WriterException;

import com.trello.rxlifecycle2.LifecycleTransformer;

import butterknife.BindView;
import butterknife.OnClick;

public class AddressBookDetailActivity extends BaseActivity {

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.copy)
    TextView copy;
    @BindView(R.id.remark)
    EditText remark;


    private AddressBookTb addressBookTb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book_detail);
        addressBookTb = GsonUtils.getObjFromJSON(getIntent().getStringExtra(Constants.INTENT_DATA), AddressBookTb.class);
        setTitle(addressBookTb.getCoin());
        try {
            String fullName = ConfigUtil.getInstance().getCoinFullName(addressBookTb.getCoin());
            img.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, fullName+":"+addressBookTb.getAddress()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        address.setText(addressBookTb.getAddress());
        remark.setText(addressBookTb.getName());
        remark.setSelection(remark.getText().length());
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(remark);
                finish();
            }
        });
        remark.setFilters(new InputFilter[]{new EditLengthInputFilter(12)});
        setRightText(getString(R.string.save_str), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EmojiFilter.containsEmoji(remark.getText().toString())){
                    ToastUtil.showShort(getActivity(), getString(R.string.cannot_emoji));
                    return;
                }
                DialogUtil.showProgressDialog(AddressBookDetailActivity.this);
                RetrofitManager.getInstance().mNetService.updateAddressBookName(new UpdateAddressBookNameRequest(addressBookTb.getCoin(),addressBookTb.getAddress(),remark.getText().toString()))
                        .compose(RxSchedulers.<BaseResponse<String>>compose())
                        .compose(bindLifecycle())
                        .subscribe(new ResponseObserver<String>() {
                            @Override
                            protected void onHandleSuccess(String datas) {
                                ToastUtil.showShort(getActivity(), R.string.update_success);
                                addressBookTb.setName(remark.getText().toString());
                                new AddressBookUtil(AddressBookDetailActivity.this).updateAddressBook(addressBookTb);
                                finish();
                            }
                        });
            }
        });
    }

    @OnClick({R.id.copy, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copy:
                Utils.copyContent(address.getText().toString(),AddressBookDetailActivity.this);
                break;
            case R.id.delete_btn:
                DialogUtil.showTextDialog(this, R.string.delete_address, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.showProgressDialog(AddressBookDetailActivity.this);
                        DeleteAddressBookRequest deleteAddressBookRequest = new DeleteAddressBookRequest();
                        deleteAddressBookRequest.addressList.add(new DeleteAddressBookRequest.DeleteAddressBook(addressBookTb.getAddress()));
                        RetrofitManager.getInstance().mNetService.deleteAddressBook(deleteAddressBookRequest)
                                .compose(RxSchedulers.<BaseResponse<String>>compose())
                                .compose(bindLifecycle())
                                .subscribe(new ResponseObserver<String>() {
                                    @Override
                                    protected void onHandleSuccess(String datas) {
                                        new AddressBookUtil(AddressBookDetailActivity.this).deleteAddressBook(addressBookTb);
                                        finish();
                                    }
                                });
                    }
                });
                break;
        }
    }

    public LifecycleTransformer bindLifecycle() {
        LifecycleTransformer objectLifecycleTransformer = bindToLifecycle();
        return objectLifecycleTransformer;
    }

}
