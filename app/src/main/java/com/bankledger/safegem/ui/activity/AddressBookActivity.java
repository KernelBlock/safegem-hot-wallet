package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.AddressBookUtil;
import com.bankledger.safegem.greendaodb.entity.AddressBookTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.response.AddressBookResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.Utils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class AddressBookActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    @BindView(R.id.seaerch_edit)
    EditText seaerchEdit;

    private AddressBookUtil addressBookUtil;
    private List<AddressBookTb> list = new ArrayList<>();
    private List<AddressBookTb> allList = new ArrayList<>();
    private BaseRecyclerAdapter<AddressBookTb> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        ButterKnife.bind(this);
        setTitle(R.string.address, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //一键置顶
                recyclerView.smoothScrollToPosition(0);
            }
        });
        setRightImage(R.drawable.add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySkipUtil.skipAnotherActivity(AddressBookActivity.this, AddAddressActivity.class);
            }
        });
    }

    @Override
    public void initView() {
        super.initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<AddressBookTb>(AddressBookActivity.this, list, R.layout.address_item_layout) {
            @Override
            public void convert(BaseRecyclerHolder holder, AddressBookTb item, int position, boolean isScrolling) {

                holder.setImageResource(R.id.img, Utils.getCoinImg(item.getCoin(), Utils.coin2Type(item.getCoin()))).setText(R.id.name_tv, item.getCoin()).setText(R.id.address, item.getAddress()).setText(R.id.remark, item.getName());
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.INTENT_DATA, GsonUtils.toString(list.get(position)));
                ActivitySkipUtil.skipAnotherActivity(AddressBookActivity.this, AddressBookDetailActivity.class, map);
            }
        });
        recyclerView.setAdapter(mAdapter);
        seaerchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    filterCountryCode(editable.toString());
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        addressBookUtil = new AddressBookUtil(this);
        list.addAll(addressBookUtil.queryAllAddressBook());
        allList.addAll(list);
        if (list.size() == 0) {
            DialogUtil.showProgressDialog(this);
            RetrofitManager.getInstance().mNetService.getAddressBook()
                    .compose(bindLifecycle())
                    .doOnNext(new Consumer<BaseResponse<List<AddressBookResponse>>>() {
                        @Override
                        public void accept(BaseResponse<List<AddressBookResponse>> response) throws Exception {
                            List<AddressBookResponse> content = response.getData();
                            List<AddressBookTb> tempList = new ArrayList<>();
                            for (AddressBookResponse addressBookResponse : content) {
                                AddressBookTb addressBookTb = new AddressBookTb(SafeGemApplication.getInstance().getUserInfo().getUserId(), addressBookResponse.coin, addressBookResponse.name, addressBookResponse.address);
                                tempList.add(addressBookTb);
                            }
                            if (tempList.size() > 0) {
                                addressBookUtil.insertAddressBook(tempList);
                            }
                            list.addAll(addressBookUtil.queryAllAddressBook());
                            allList.addAll(list);
                        }
                    })
                    .compose(RxSchedulers.<BaseResponse<List<AddressBookResponse>>>compose())
                    .subscribe(new ResponseObserver<List<AddressBookResponse>>() {
                        @Override
                        protected void onHandleSuccess(List<AddressBookResponse> content) {
                            setEmptyView();
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
            setEmptyView();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list.addAll(addressBookUtil.queryAllAddressBook());
        allList.clear();
        allList.addAll(addressBookUtil.queryAllAddressBook());
        setEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    public void filterCountryCode(String content) {
        content = content.toLowerCase();
        List<AddressBookTb> data = new ArrayList<>();
        for (AddressBookTb item : allList) {
            if (item.getCoin().trim().toLowerCase().contains(content) || item.getAddress().trim().toLowerCase().contains(content) || item.getName().trim().toLowerCase().contains(content)) {
                data.add(item);
            }
        }
        list.clear();
        list.addAll(data);
        setEmptyView();
        mAdapter.notifyDataSetChanged();
    }

    public void setEmptyView() {
        if (list.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            tvNullData.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNullData.setVisibility(View.GONE);
        }
    }

    public LifecycleTransformer bindLifecycle() {
        return bindToLifecycle();
    }

}
