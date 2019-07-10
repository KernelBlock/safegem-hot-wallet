package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.presenter.ERCPresenter;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ERCView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ERCActivity extends MVPBaseActivity<ERCView, ERCPresenter> implements ERCView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    @BindView(R.id.search)
    EditText search;
    private List<ERCResponse.ERC> list;
    private BaseRecyclerAdapter<ERCResponse.ERC> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erc);
        setTitle(getString(R.string.erc20_str));
        setRightImage(R.drawable.add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SafeGemApplication.getInstance().getColdUniqueId().length() == 0) {
                    ToastUtil.showShort(getActivity(), getString(R.string.please_add_wallet));
                    return;
                }
                if (SafeGemApplication.getInstance().getCurrentETHAddress().length() == 0) {
                    ToastUtil.showShort(getActivity(), getString(R.string.please_eth_address));
                    return;
                }
                Intent intent = new Intent(ERCActivity.this, AddERCTokenActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });
        list = new ArrayList<ERCResponse.ERC>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<ERCResponse.ERC>(this, list, R.layout.erc20_item_layout) {
            @Override
            public void convert(BaseRecyclerHolder holder, ERCResponse.ERC item, int position, boolean isScrolling) {
                if (item.getActivication() == 1){
                    holder.getView(R.id.content_view).setBackgroundColor(Utils.getColor(R.color.bg));
                }else {
                    holder.getView(R.id.content_view).setBackgroundColor(Utils.getColor(R.color.white));
                }
                holder.setImageUrl(R.id.img, item.getIcon(), R.drawable.etc_icon)
                        .setText(R.id.name_tv, item.getSymbol())
                        .setText(R.id.address, item.getContractAddress());
            }
        };

        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent(ERCActivity.this, CurrencyActivity.class);
                intent.putExtra(Constants.INTENT_DATA, list.get(position));
                startActivityForResult(intent,Constants.REQUEST_CODE);
            }
        });
        recyclerView.setAdapter(mAdapter);
        mPresenter.getERC20Token();
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyboard(search);
                    mPresenter.searchERC20Token(search.getText().toString());
                    return true;
                }
                return false;
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPresenter.searchERC20Token(search.getText().toString());
            }
        });


    }

    @Override
    protected ERCPresenter createPresenter() {
        return new ERCPresenter(this);
    }


    @Override
    public void onGetERCComplete(List<ERCResponse> content) {
        list.clear();
        for (ERCResponse ercResponse : content) {
            list.addAll(ercResponse.erc20List);
        }
        mAdapter.notifyDataSetChanged();
        refreshUI();
    }

    public void refreshUI() {
        if (list.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tvNullData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNullData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE) {
                mPresenter.getERC20Token();
        }
    }
}
