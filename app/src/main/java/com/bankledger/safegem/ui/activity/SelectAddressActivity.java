package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.Utils;

import java.util.List;

import butterknife.BindView;

public class SelectAddressActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int coinType;
    private String coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coin);
        setTitle(getString(R.string.select_address));
        coinType = getIntent().getIntExtra(Constants.INTENT_KEY1, 0);
        coin = getIntent().getStringExtra(Constants.INTENT_KEY2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        MonitorAddressUtil monitorAddressUtil = new MonitorAddressUtil(this);
        List<MonitorAddressTb> list = monitorAddressUtil.queryUserCoinMonitorAddressTbs(coin, coinType);
        BaseRecyclerAdapter<MonitorAddressTb> adapter = new BaseRecyclerAdapter<MonitorAddressTb>(this, list, R.layout.select_address_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, MonitorAddressTb item, int position, boolean isScrolling) {
                holder.setText(R.id.name_tv, item.getCoin()).setText(R.id.address, item.getAddress());
                if (item.getCoinType() == Constants.COIN_ETH_TOKEN) {
                    holder.setText(R.id.name_tv, item.getSymbol()).setImageUrl(R.id.img, item.getCoinImg(), R.mipmap.app_launcher);
                } else {
                    holder.setImageResource(R.id.img, Utils.getCoinImg(item.getCoin(), item.getCoinType()));
                }
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(Constants.INTENT_DATA, GsonUtils.toString(list.get(position)));
                setResult(Constants.MONITOR_ADDRESS, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}