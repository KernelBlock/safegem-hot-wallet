package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.presenter.TransactionCoinPresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.MoreRequestDialogUtil;
import com.bankledger.safegem.utils.RecyclerViewDivider;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ITransactionCoinView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class TransactionCoinActivity extends MVPBaseActivity<ITransactionCoinView, TransactionCoinPresenter> implements ITransactionCoinView {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    private List<CoinEntity> coinList = new ArrayList<>();

    private BaseRecyclerAdapter<CoinEntity> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_coin);
        setTitle(getString(R.string.trade_str));
        if (SafeGemApplication.getInstance().getColdUniqueId().length() == 0) {
            ToastUtil.showShort(this, getString(R.string.please_add_wallet));
            finish();
        }
    }

    @Override
    protected TransactionCoinPresenter createPresenter() {
        return new TransactionCoinPresenter(this);
    }

    @Override
    public void initView() {
        super.initView();
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new BaseRecyclerAdapter<CoinEntity>(this, coinList, 0) {

            @Override
            public int getItemViewType(int position) {
                CoinEntity item = coinList.get(position);
                if (item.coinType == Constants.COIN_BTC
                        || item.coinType == Constants.COIN_ETH
                        || item.coinType == Constants.COIN_ETC
                        || item.coinType == Constants.COIN_EOS
                        || item.coinType == Constants.COIN_USDT) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                if (viewType == 0) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_coin_item, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.token_select_coin_item, parent, false);
                }
                return BaseRecyclerHolder.getRecyclerHolder(parent.getContext(), view);
            }

            @Override
            public void convert(BaseRecyclerHolder holder, CoinEntity item, int position, boolean isScrolling) {
                holder.setText(R.id.name_tv, StringUtil.getDisplayName(item.coin));

                holder.setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType));
                if (item.coinType == Constants.COIN_EOS_TOKEN) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_EOS) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                } else if (item.coinType == Constants.COIN_ETH_TOKEN) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_ETH) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                    holder.setImageUrl(R.id.img, item.coinImg, R.drawable.eth_img);
                } else if (item.coinType == Constants.COIN_SAFE_ASSET) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_BTC) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                    holder.setText(R.id.name_tv, StringUtil.getDisplayName(item.assetName));
                }
            }
        };

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                CoinEntity item = coinList.get(position);
                HashMap<String, Object> map = new HashMap<>();
                map.put(Constants.INTENT_KEY1, GsonUtils.toString(item));
                ActivitySkipUtil.skipAnotherActivity(TransactionCoinActivity.this, TransactionRecordActivity.class, map);
            }
        });
        rvList.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        super.initData();
        MoreRequestDialogUtil.showProgressDialog(this);
        mPresenter.getTransactionCoin(SafeGemApplication.getInstance().getUserId(), SafeGemApplication.getInstance().getColdUniqueId());
    }

    @Override
    public void onGetMonitorAddr(List<CoinEntity> coinList) {
        MoreRequestDialogUtil.dismissProgressDialog();
        if (coinList.size() > 0) {
            rvList.setVisibility(View.VISIBLE);
            tvNullData.setVisibility(View.GONE);
        } else {
            rvList.setVisibility(View.GONE);
            tvNullData.setVisibility(View.VISIBLE);
        }
        this.coinList.addAll(coinList);
        mAdapter.notifyDataSetChanged();
    }

}
