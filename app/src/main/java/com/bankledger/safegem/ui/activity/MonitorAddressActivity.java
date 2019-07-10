package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.protobuf.bean.TransMulAddress;
import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.presenter.MonitorAddressPresenter;
import com.bankledger.safegem.scan.ScanActivity;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.MoreRequestDialogUtil;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.UpdateBanlanceUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.IMonitorAddressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.ObservableSource;

public class MonitorAddressActivity extends MVPBaseActivity<IMonitorAddressView, MonitorAddressPresenter> implements IMonitorAddressView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.coin_count)
    TextView coinCount;

    @BindView(R.id.tb)
    ToggleButton tb;

    @BindView(R.id.empty)
    TextView empty;

    private List<MonitorAddressEntity> filterlist;
    private List<MonitorAddressEntity> allList;
    private BaseRecyclerAdapter<MonitorAddressEntity> mAdapter;
    private String coldUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_address);
        setTitle(R.string.monitor_title);
        coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        filterlist = new ArrayList();
        allList = new ArrayList();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<MonitorAddressEntity>(this, filterlist, 0) {
            @Override
            public int getItemViewType(int position) {
                MonitorAddressEntity item = list.get(position);
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
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_item_layout, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.token_item_layout, parent, false);
                }
                return BaseRecyclerHolder.getRecyclerHolder(parent.getContext(), view);
            }

            @Override
            public void convert(BaseRecyclerHolder holder, MonitorAddressEntity item, int position, boolean isScrolling) {
                holder.setText(R.id.name_tv, StringUtil.getDisplayName(item.name));
                if (item.coinType == Constants.COIN_BTC) {
                    String amount = BigDecimalUtils.formatBtc(item.ethAmount);
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, amount);
                } else if (item.coinType == Constants.COIN_ETC) {
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_ETH) {
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_EOS) {
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_USDT) {
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_EOS_TOKEN) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_EOS) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_ETH_TOKEN) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_ETH) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageUrl(R.id.img, item.coinImg, R.drawable.eth_img).setText(R.id.num_tv1, item.ethAmount);
                } else if (item.coinType == Constants.COIN_SAFE_ASSET) {
                    if (position > 0 && list.get(position - 1).coinType == Constants.COIN_BTC) {
                        holder.setVisible(R.id.line, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.line, View.INVISIBLE);
                    }
                    holder.setText(R.id.count, item.count + getString(R.string.address_count)).setImageResource(R.id.img, Utils.getCoinImg(item.coin, item.coinType)).setText(R.id.num_tv1, item.ethAmount);
                }
            }
        };
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.INTENT_DATA, GsonUtils.toString(filterlist.get(position)));
                ActivitySkipUtil.skipAnotherActivity(MonitorAddressActivity.this, MonitorAddressDetailActivity.class, map);
            }
        });
        recyclerView.setAdapter(mAdapter);
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPresenter.getColdMonitorAddress();

        setRightImage(R.drawable.add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(MonitorAddressActivity.this, ScanActivity.class);
                        intent.putExtra(Constants.INTENT_KEY2, getString(R.string.monitor_tip_content));
                        intent.putExtra(Constants.SCAN_FLAG, Constants.ADD_ADDRESS);
                        startActivityForResult(intent, Constants.REQUEST_CODE);
                    }

                    @Override
                    public void onDenied() {
                    }
                });
            }
        });

        UpdateBanlanceUtil.getInstance().updateWalletBalance(coldUniqueId, new OnUpdateBanlanceListener() {
            @Override
            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                mPresenter.getColdMonitorAddress();
                return null;
            }
        });
    }

    @Override
    protected MonitorAddressPresenter createPresenter() {
        return new MonitorAddressPresenter(this);
    }

    @Override
    public void initView() {
        super.initView();
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isHidden) {
                if (isHidden) {
                    Iterator<MonitorAddressEntity> iterator = filterlist.iterator();
                    while (iterator.hasNext()) {
                        MonitorAddressEntity monitorAddressEntity = iterator.next();
                        String amount = TextUtils.isEmpty(monitorAddressEntity.ethAmount) ? "0" : monitorAddressEntity.ethAmount;
                        if (!BigDecimalUtils.greaterThan(amount, "0")) {
                            iterator.remove();
                        }
                    }
                } else {
                    filterlist.clear();
                    filterlist.addAll(allList);
                }
                coinCount.setText(filterlist.size() + getString(R.string.curern_str));
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onGetMonitorAddressSuccess(List<MonitorAddressEntity> monitorAddressBeans) {
        filterlist.clear();
        filterlist.addAll(monitorAddressBeans);
        allList.clear();
        allList.addAll(monitorAddressBeans);
        if (tb.isChecked()) {
            Iterator<MonitorAddressEntity> iterator = filterlist.iterator();
            while (iterator.hasNext()) {
                MonitorAddressEntity monitorAddressEntity = iterator.next();
                String amount = TextUtils.isEmpty(monitorAddressEntity.ethAmount) ? "0" : monitorAddressEntity.ethAmount;
                if (!BigDecimalUtils.greaterThan(amount, "0")) {
                    iterator.remove();
                }
            }
        } else {
            if (filterlist.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        }
        coinCount.setText(filterlist.size() + getString(R.string.curern_str));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateWalletComplete(boolean isAdd) {
        tb.setChecked(false);
        if (isAdd) {
            MoreRequestDialogUtil.showProgressDialog(this);
            UpdateBanlanceUtil.getInstance().updateWalletBalance(coldUniqueId, new OnUpdateBanlanceListener() {
                @Override
                public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                    mPresenter.getColdMonitorAddress();
                    MoreRequestDialogUtil.dismissProgressDialog();
                    return null;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.TYPE_MON:
                TransMulAddress transMulAddress = (TransMulAddress) data.getSerializableExtra(Constants.SCAN_DATA);
                if (!transMulAddress.walletSeqNumber.equals(coldUniqueId)) {
                    ToastUtil.showShort(this, getString(R.string.please_go_wallet_monitor));
                } else {
                    mPresenter.addColdMonitorAddress(transMulAddress);
                }
                break;
        }
    }
}
