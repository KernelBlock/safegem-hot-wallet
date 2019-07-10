package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.net.model.response.BestFeeResponse;
import com.bankledger.safegem.presenter.GetBestFeePresenter;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.RecyclerViewDivider;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.IGetBestFeeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author bankledger
 * @time 2018/8/31 13:32
 */
public class BestFeeActivity extends MVPBaseActivity<IGetBestFeeView, GetBestFeePresenter> implements IGetBestFeeView {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    @BindView(R.id.srlayout)
    SwipeRefreshLayout srLayout;

    private BaseRecyclerAdapter<BestFeeResponse> mAdapter;

    private List<BestFeeResponse> feeList = new ArrayList<>();

    @Override
    protected GetBestFeePresenter createPresenter() {
        return new GetBestFeePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_fee);
        setTitle(R.string.fee_str);
    }

    @Override
    public void initView() {
        super.initView();
        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getBestFee();
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new BaseRecyclerAdapter<BestFeeResponse>(this, feeList, R.layout.best_fee_item_layout) {
            @Override
            public void convert(BaseRecyclerHolder holder, BestFeeResponse item, int position, boolean isScrolling) {
                holder.setImageResource(R.id.iv_logo, Utils.getCoinImg(item.coin, Utils.coin2Type(item.coin)));
                holder.setText(R.id.tv_coin, StringUtil.getDisplayName(item.coin));
                if (item.coin.equals(Constants.ETH)) {
                    holder.setText(R.id.tv_fee, item.fee + "\t" + item.unit);
                } else {
                    holder.setText(R.id.tv_fee, String.format(getString(R.string.best_fee_value), item.fee, item.coin, item.unit));
                }

            }
        };
        rvList.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        super.initData();
        mPresenter.getBestFee();
    }

    @Override
    public void onError() {
        srLayout.setRefreshing(false);
        refreshUI();
    }

    @Override
    public void onSuccess(List<BestFeeResponse> fees) {
        srLayout.setRefreshing(false);
        feeList.clear();
        feeList.addAll(fees);
        mAdapter.notifyDataSetChanged();
        refreshUI();
    }

    public void refreshUI() {
        if (feeList.size() > 0) {
            srLayout.setVisibility(View.VISIBLE);
            tvNullData.setVisibility(View.GONE);
        } else {
            srLayout.setVisibility(View.GONE);
            tvNullData.setVisibility(View.VISIBLE);
        }
    }
}
