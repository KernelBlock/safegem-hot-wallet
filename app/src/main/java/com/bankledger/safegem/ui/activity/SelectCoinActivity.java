package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class SelectCoinActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    String coldUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coin);
        setTitle(getString(R.string.select_coin));
        coldUniqueId = getIntent().getStringExtra(Constants.INTENT_DATA);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (coldUniqueId != null) {
            String userId = SafeGemApplication.getInstance().getUserId();
            MonitorAddressUtil monitorUtil = new MonitorAddressUtil(this);
            EosBalanceUtil eosUtil = new EosBalanceUtil(this);
            SafeAssetUtil safeAssetUtil = new SafeAssetUtil(this);
            List<CoinEntity> coinList = new ArrayList<>();
            List<MonitorAddressTb> addressList = monitorUtil.queryCurrentMonitorNoEthToken(userId, coldUniqueId);
            for (MonitorAddressTb addressTb : addressList) {
                CoinEntity parent = new CoinEntity();
                parent.coin = addressTb.getCoin();
                parent.coinType = addressTb.getCoinType();
                coinList.add(parent);
                if (addressTb.getCoinType() == Constants.COIN_EOS) { // 处理EOS代币
                    List<EosBalanceTb> eosBalanceTbs = eosUtil.queryEosTokenBalanceTb(SafeGemApplication.getInstance().getCurrentEOSAccount());
                    for (EosBalanceTb item : eosBalanceTbs) {
                        CoinEntity child = new CoinEntity();
                        child.coin = item.getTokenName();
                        child.coinType = Constants.COIN_EOS_TOKEN;
                        coinList.add(child);
                    }
                } else if (addressTb.getCoinType() == Constants.COIN_ETH) { //处理ETH代币
                    List<MonitorAddressTb> tokenAddressTbs = monitorUtil.queryUserCoinEthTokenMonitorAddressTb();
                    for (MonitorAddressTb item : tokenAddressTbs) {
                        CoinEntity child = new CoinEntity();
                        child.coin = item.getSymbol();
                        child.coinType = Constants.COIN_ETH_TOKEN;
                        child.coinImg = item.getCoinImg();
                        coinList.add(child);
                    }
                } else if (addressTb.getCoin().equals(Constants.SAFE)) {  // 处理SAFE代币
                    List<SafeAssetTb> safeAssetTbs = safeAssetUtil.getSafeAssetList(addressTb.getCoin());
                    for (SafeAssetTb item : safeAssetTbs) {
                        CoinEntity child = new CoinEntity();
                        child.coin = Constants.SAFE;
                        child.coinType = Constants.COIN_SAFE_ASSET;
                        child.assetId = item.getAssetId();
                        child.assetName = item.getAssetName();
                        coinList.add(child);
                    }
                }
            }
            BaseRecyclerAdapter<CoinEntity> adapter = new BaseRecyclerAdapter<CoinEntity>(this, coinList, 0) {

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
            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.INTENT_DATA, GsonUtils.toString(coinList.get(position)));
                    setResult(Constants.RESULT_CODE, intent);
                    finish();
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            List<String> coinList = Arrays.asList(Constants.Companion.getCOIN_ARR());
            BaseRecyclerAdapter adapter = new BaseRecyclerAdapter<String>(this, coinList, R.layout.select_coin_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                    holder.setText(R.id.name_tv, StringUtil.getDisplayName(item)).setImageResource(R.id.img, Utils.getCoinImg(item, Utils.coin2Type(item)));
                }
            };
            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.INTENT_DATA, coinList.get(position));
                    setResult(Constants.RESULT_CODE, intent);
                    finish();
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
