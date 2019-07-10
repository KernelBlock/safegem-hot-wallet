package com.bankledger.safegem.ui.activity;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.entity.TransactionRecordBean;
import com.bankledger.safegem.greendaodb.EosRamUtil;
import com.bankledger.safegem.greendaodb.EosTxUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.EosRamTb;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.presenter.TransactionRecordPresenter;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.MoreRequestDialogUtil;
import com.bankledger.safegem.utils.RecyclerViewDivider;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.UpdateBanlanceUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ITransactionRecordView;
import com.zss.library.LoadMoreRecyclerView;
import com.zss.library.adapter.CommonAdapter;
import com.zss.library.adapter.ViewHolder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.ObservableSource;

public class TransactionRecordActivity extends MVPBaseActivity<ITransactionRecordView, TransactionRecordPresenter> implements ITransactionRecordView {

    @BindView(R.id.sr_layout)
    SwipeRefreshLayout srLayout;

    @BindView(R.id.rv_list)
    LoadMoreRecyclerView rvList;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    private CoinEntity coinEntity;

    private List<TransactionRecordBean> btcFilterList = new ArrayList<>();
    private CommonAdapter<TransactionRecordBean> mBtcAdapter;

    private List<EosTxTb> eosFilterList = new ArrayList<>();
    private CommonAdapter<EosTxTb> mEosAdapter;

    private List<UsdtTxTb> usdtFilterList = new ArrayList<>();
    private CommonAdapter<UsdtTxTb> mUsdtAdapter;

    private Map<String, EosRamTb> eosRamMap = new HashMap<>();

    private int pageIndex = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_record);
        setTitle(getString(R.string.trade_str), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //一键置顶
                rvList.smoothScrollToPosition(0);
            }
        });
        if (SafeGemApplication.getInstance().getColdUniqueId().length() == 0) {
            ToastUtil.showShort(getActivity(), getString(R.string.please_add_wallet));
            finish();
        }
    }

    @Override
    protected TransactionRecordPresenter createPresenter() {
        return new TransactionRecordPresenter(this);
    }

    @Override
    public void initView() {
        super.initView();
        coinEntity = GsonUtils.getObjFromJSON(getIntent().getStringExtra(Constants.INTENT_KEY1), CoinEntity.class);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, false));
        if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) {
            mBtcAdapter = new CommonAdapter<TransactionRecordBean>(0, btcFilterList) {
                @Override
                public int getItemViewType(int position) {
                    TransactionRecordBean transactionRecordBean = btcFilterList.get(position);
                    if (position == 0) {
                        return 1;
                    } else {
                        if (btcFilterList.get(position - 1).date.equals(transactionRecordBean.date)) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }

                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType == 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_item, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_with_date_item, parent, false);
                    }
                    return new ViewHolder(view);
                }

                @Override
                protected void convert(ViewHolder viewHolder, TransactionRecordBean item, int position) {
                    if (getItemViewType(position) == 1) {
                        TextView tvDate = viewHolder.findViewById(R.id.tv_date);
                        tvDate.setText(item.date);
                        //点击时间时不跳转，这里将事件消费掉
                        tvDate.setOnClickListener(null);
                    }
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource((Utils.getCoinImg(item.coin, item.coinType)));
                    TextView time = viewHolder.findViewById(R.id.time);
                    time.setText(item.time);
                    TextView tvCoin = viewHolder.findViewById(R.id.tv_coin);
                    if (coinEntity.coinType == Constants.COIN_SAFE_ASSET) {
                        tvCoin.setText(StringUtil.getDisplayName(coinEntity.assetName));
                    } else {
                        tvCoin.setText(StringUtil.getDisplayName(item.coin));
                    }
                    TextView tvAddress = viewHolder.findViewById(R.id.tv_address);
                    tvAddress.setText(item.address);
                    TextView tvAmount = viewHolder.findViewById(R.id.tv_amount);
                    String amount = BigDecimalUtils.formatBtc(item.amount);
                    if (BigDecimalUtils.greaterThan(amount, "0")) {
                        tvAmount.setTextColor(Utils.getColor(R.color.title_bar_bg));
                        amount = "+" + amount;
                    } else {
                        tvAmount.setTextColor(ContextCompat.getColor(TransactionRecordActivity.this, R.color.black));
                    }
                    tvAmount.setText(StringUtil.subZeroAndDot(amount));
                }

                @Override
                protected void onItemClick(View view, TransactionRecordBean transactionRecordBean, int position) {
                    HashMap<String, Object> args = new HashMap<>(1);
                    args.put(Constants.INTENT_KEY1, btcFilterList.get(position).txHash);
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, coinEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(TransactionRecordActivity.this, TradeDetailActivity.class, args);
                }


            };
            rvList.setAdapter(mBtcAdapter);
        } else if (coinEntity.coinType == Constants.COIN_USDT) {
            String address = SafeGemApplication.getInstance().getCurrentUSDTAddress();
            mUsdtAdapter = new CommonAdapter<UsdtTxTb>(0, usdtFilterList) {
                @Override
                public int getItemViewType(int position) {
                    UsdtTxTb eosTxTb = usdtFilterList.get(position);
                    if (position == 0) {
                        return 1;
                    } else {
                        if (usdtFilterList.get(position - 1).getDate().equals(eosTxTb.getDate())) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }

                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType == 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_item, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_with_date_item, parent, false);
                    }
                    return new ViewHolder(view);
                }

                @Override
                protected void convert(ViewHolder viewHolder, UsdtTxTb item, int position) {
                    if (getItemViewType(position) == 1) {
                        TextView tvDate = viewHolder.findViewById(R.id.tv_date);
                        tvDate.setText(item.getDate());
                        //点击时间时不跳转，这里将事件消费掉
                        tvDate.setOnClickListener(null);
                    }
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource((Utils.getCoinImg(Constants.USDT, Constants.COIN_USDT)));
                    TextView time = viewHolder.findViewById(R.id.time);
                    time.setText(item.getFormatTime());
                    TextView tvCoin = viewHolder.findViewById(R.id.tv_coin);
                    tvCoin.setText(Constants.USDT);
                    TextView tvAddress = viewHolder.findViewById(R.id.tv_address);
                    TextView tvAmount = viewHolder.findViewById(R.id.tv_amount);
                    tvAddress.setText(item.getReferenceaddress());
                    if (item.getSendingaddress().equals(address)) {
                        tvAmount.setText("-" + item.getAmount());
                        tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_color));
                    } else {
                        tvAmount.setText("+" + item.getAmount());
                        tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                    }
                }

                @Override
                protected void onItemClick(View view, UsdtTxTb usdtTxTb, int position) {
                    HashMap<String, Object> args = new HashMap<>(1);
                    args.put(Constants.INTENT_KEY1, usdtFilterList.get(position).getTxid());
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, coinEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(TransactionRecordActivity.this, TradeDetailActivity.class, args);
                }


            };
            rvList.setAdapter(mUsdtAdapter);
        } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) {
            mEosAdapter = new CommonAdapter<EosTxTb>(0, eosFilterList) {
                @Override
                public int getItemViewType(int position) {
                    EosTxTb eosTxTb = eosFilterList.get(position);
                    if (position == 0) {
                        return 1;
                    } else {
                        if (eosFilterList.get(position - 1).getDate().equals(eosTxTb.getDate())) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }

                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType == 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_item, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tranc_record_with_date_item, parent, false);
                    }
                    return new ViewHolder(view);
                }

                @Override
                protected void convert(ViewHolder viewHolder, EosTxTb item, int position) {
                    if (getItemViewType(position) == 1) {
                        TextView tvDate = viewHolder.findViewById(R.id.tv_date);
                        tvDate.setText(item.getDate());
                        //点击时间时不跳转，这里将事件消费掉
                        tvDate.setOnClickListener(null);
                    }
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource(Utils.getCoinImg(item.getCoin(), Constants.COIN_EOS));
                    TextView time = viewHolder.findViewById(R.id.time);
                    time.setText(item.getFormatTime());
                    TextView tvCoin = viewHolder.findViewById(R.id.tv_coin);
                    tvCoin.setText(item.getCoin());
                    TextView tvAddress = viewHolder.findViewById(R.id.tv_address);
                    TextView tvAmount = viewHolder.findViewById(R.id.tv_amount);
                    if (item.getType() == 1) { //购买内存
                        EosRamTb ramTb = eosRamMap.get(item.getTxId());
                        tvAddress.setText(ramTb.getTo());
                    } else {
                        tvAddress.setText(item.getTo());
                    }
                    if (item.getFrom().equals(item.getAccount())) {
                        if (item.getType() == 1) { //购买内存
                            EosRamTb ramTb = eosRamMap.get(item.getTxId());
                            String amount = ramTb.getQuantity().split(" ")[0];
                            tvAmount.setText("-" + BigDecimalUtils.add(amount, item.getAmount()));
                        } else {
                            tvAmount.setText("-" + item.getAmount());
                        }
                        tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_color));
                    } else {
                        if (item.getType() == 1) { //购买内存
                            EosRamTb ramTb = eosRamMap.get(item.getTxId());
                            String amount = ramTb.getQuantity().split(" ")[0];
                            tvAmount.setText("+" + BigDecimalUtils.add(amount, item.getAmount()));
                        } else {
                            tvAmount.setText("+" + item.getAmount());
                        }
                        tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                    }
                }

                @Override
                protected void onItemClick(View view, EosTxTb eosTxTb, int position) {
                    HashMap<String, Object> args = new HashMap<>(1);
                    args.put(Constants.INTENT_KEY1, eosFilterList.get(position).getTxId());
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, coinEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(TransactionRecordActivity.this, TradeDetailActivity.class, args);
                }

            };
            rvList.setAdapter(mEosAdapter);
        }

        rvList.setVisibility(View.GONE);
        tvNullData.setVisibility(View.GONE);
        srLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
    }

    @Override
    public void initData() {
        super.initData();
        if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
            MonitorAddressUtil addrUtil = new MonitorAddressUtil(this);
            List<String> adddressList = addrUtil.queryUserCoinMonitorAddress(coinEntity.coin);
            MoreRequestDialogUtil.showProgressDialog(this);
            UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(adddressList), coinEntity.coin, new OnUpdateBanlanceListener() {
                @Override
                public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                    mPresenter.getBtcLocalTransaction(coinEntity.coin, coinEntity.assetId, pageIndex);
                    MoreRequestDialogUtil.dismissProgressDialog();
                    return null;
                }
            });
        } else if (coinEntity.coinType == Constants.COIN_USDT) { // USDT
            String address = SafeGemApplication.getInstance().getCurrentUSDTAddress();
            mPresenter.getUsdtTransactionRecord(address);
            mPresenter.getUsdtLocalTransactionRecord(pageIndex);
        } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) { // EOS
            String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
            EosTxUtil txTbUtil = new EosTxUtil(this);
            Long position = txTbUtil.queryEosTxCount(account);
            if (position > 0L) {
                position++;
            }
            mPresenter.getEosTransactionRecord(account, position);
            mPresenter.getEosLocalTransactionRecord(account, coinEntity.coin, pageIndex);
        }

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
                    mPresenter.getBtcLocalTransaction(coinEntity.coin, coinEntity.assetId, pageIndex);
                } else if (coinEntity.coinType == Constants.COIN_USDT) { // USDT
                    mPresenter.getUsdtLocalTransactionRecord(pageIndex);
                } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) { // EOS
                    String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
                    mPresenter.getEosLocalTransactionRecord(account, coinEntity.coin, pageIndex);
                }
            }
        });
        rvList.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
                    mPresenter.getBtcLocalTransaction(coinEntity.coin, coinEntity.assetId, pageIndex);
                } else if (coinEntity.coinType == Constants.COIN_USDT) { // USDT
                    mPresenter.getUsdtLocalTransactionRecord(pageIndex);
                } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) { // EOS
                    String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
                    mPresenter.getEosLocalTransactionRecord(account, coinEntity.coin, pageIndex);
                }
            }
        });

    }

    private void refreshUI() {
        if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) {
            if (btcFilterList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            mBtcAdapter.notifyDataSetChanged();
        } else if (coinEntity.coinType == Constants.COIN_USDT) {
            if (usdtFilterList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            mUsdtAdapter.notifyDataSetChanged();
        } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) {
            if (eosFilterList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            mEosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetTransactionRecord(List<TransactionRecordBean> recordBeans) {
        if (pageIndex == 1) {
            btcFilterList.clear();
            btcFilterList.addAll(recordBeans);
        } else {
            btcFilterList.addAll(recordBeans);
        }
        if (recordBeans.size() == 0) {
            rvList.setNoMoreData(false);
        } else {
            rvList.setLoadComplete(); //加载完成
        }
        srLayout.setRefreshing(false); //刷新完成
        refreshUI();

    }

    @Override
    public void onGetEosTransactionRecord(List<EosTxTb> recordBeans) {
        EosRamUtil eosRamUtil = new EosRamUtil(this);
        for (EosTxTb item : recordBeans) {
            if (item.getType() == 1) {
                EosRamTb ramTb = eosRamUtil.queryEosRamTbForTxId(item.getTxId());
                eosRamMap.put(item.getTxId(), ramTb);
            }
        }
        if (pageIndex == 1) {
            eosFilterList.clear();
            eosFilterList.addAll(recordBeans);
        } else {
            eosFilterList.addAll(recordBeans);
        }
        if (recordBeans.size() == 0) {
            rvList.setNoMoreData(false);
        } else {
            rvList.setLoadComplete(); //加载完成
        }
        srLayout.setRefreshing(false); //刷新完成
        refreshUI();

    }

    @Override
    public void onGetUsdtTransactionRecord(List<UsdtTxTb> recordBeans) {
        if (pageIndex == 1) {
            usdtFilterList.clear();
            usdtFilterList.addAll(recordBeans);
        } else {
            usdtFilterList.addAll(recordBeans);
        }
        if (recordBeans.size() == 0) {
            rvList.setNoMoreData(false);
        } else {
            rvList.setLoadComplete(); //加载完成
        }
        srLayout.setRefreshing(false); //刷新完成
        refreshUI();
    }

    @Override
    public void onTxLoadFinish() {
        MoreRequestDialogUtil.dismissProgressDialog();
        if (coinEntity.coinType == Constants.COIN_BTC || coinEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
            btcFilterList.clear();
            pageIndex = 1;
            mPresenter.getBtcLocalTransaction(coinEntity.coin, coinEntity.assetId, pageIndex);
        } else if (coinEntity.coinType == Constants.COIN_USDT) { // USDT
            usdtFilterList.clear();
            pageIndex = 1;
            mPresenter.getUsdtLocalTransactionRecord(pageIndex);
        } else if (coinEntity.coinType == Constants.COIN_EOS || coinEntity.coinType == Constants.COIN_EOS_TOKEN) { // EOS
            eosFilterList.clear();
            pageIndex = 1;
            String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
            mPresenter.getEosLocalTransactionRecord(account, coinEntity.coin, pageIndex);
        }
    }

}
