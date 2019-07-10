package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.net.model.response.CountryCodeResponse;
import com.bankledger.safegem.presenter.CountryCodePresenter;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.view.ICountryCodeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryCodeActivity extends MVPBaseActivity<ICountryCodeView, CountryCodePresenter> implements ICountryCodeView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.search)
    EditText search;
    private List<CountryCodeResponse> list;
    private BaseRecyclerAdapter<CountryCodeResponse> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);
        setTitle(R.string.country_str);
        mPresenter.getCountryCode();
    }

    @Override
    public void initView() {
        super.initView();
        list = new ArrayList<>();
        mAdapter = new BaseRecyclerAdapter<CountryCodeResponse>(this, list, R.layout.country_item_layout) {
            @Override
            public void convert(BaseRecyclerHolder holder, CountryCodeResponse item, int position, boolean isScrolling) {
                holder.setText(R.id.code, item.code).setText(R.id.zh, item.zh).setText(R.id.en, item.en);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(Constants.RESPONSE_DATA, list.get(position).code);
                setResult(Constants.RESULT_CODE, intent);
                finish();
            }
        });
        recyclerView.setAdapter(mAdapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())){
                    mPresenter.filterCountryCode(editable.toString());
                }
            }
        });
    }

    @Override
    protected CountryCodePresenter createPresenter() {
        return new CountryCodePresenter(this);
    }


    @Override
    public void onGetCountryCode(List<CountryCodeResponse> data) {
        list.clear();
        list.addAll(data);
        mAdapter.notifyDataSetChanged();
    }
}
