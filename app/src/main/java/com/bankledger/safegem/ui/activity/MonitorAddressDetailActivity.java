package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.bankledger.safegem.entity.TransactionRecordBean;
import com.bankledger.safegem.greendaodb.EosRamUtil;
import com.bankledger.safegem.greendaodb.EosTxUtil;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.EthTokenUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.entity.EosRamTb;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.TxOutsTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.presenter.TransactionRecordPresenter;
import com.bankledger.safegem.ui.view.GallerySnapHelper;
import com.bankledger.safegem.ui.view.IndicatorView;
import com.bankledger.safegem.ui.view.QRCodeWindow;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.CreateCodeContentUtils;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.EthUtil;
import com.bankledger.safegem.utils.GsonUtils;
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

public class MonitorAddressDetailActivity extends MVPBaseActivity<ITransactionRecordView, TransactionRecordPresenter> implements ITransactionRecordView {

    @BindView(R.id.banner_list)
    RecyclerView bannerList;

    @BindView(R.id.sr_layout)
    SwipeRefreshLayout srLayout;

    @BindView(R.id.rv_list)
    LoadMoreRecyclerView rvList;

    @BindView(R.id.tv_null_data)
    TextView tvNullData;

    @BindView(R.id.amout_tv)
    TextView amoutTv;

    @BindView(R.id.coin_tv)
    TextView coinTv;

    @BindView(R.id.banner_dot_view)
    IndicatorView indicarView;

    private List<MonitorAddressTb> banerDatas;
    private BaseRecyclerAdapter<MonitorAddressTb> bannerAdapter;
    private GallerySnapHelper mGallerySnapHelper;
    private MonitorAddressEntity monitorAddressEntity;

    private TxUtil txUtil;
    private EthTokenUtil ethTokenUtil;
    private EosBalanceUtil eosUtil;
    private EosTxUtil eosTxUtil;
    private MonitorAddressUtil monitorAddressUtil;
    private SafeAssetUtil safeAssetUtil;

    private List<TransactionRecordBean> btcList;
    private CommonAdapter<TransactionRecordBean> btcAdapter;

    private List<EosTxTb> eosList;
    private CommonAdapter<EosTxTb> eosAdapter;

    private List<UsdtTxTb> usdtList;
    private CommonAdapter<UsdtTxTb> usdtAdapter;

    private Map<String, EosRamTb> eosRamMap = new HashMap<>();

    private int pageIndex = 1;

    QRCodeWindow qrCodeWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);
        monitorAddressEntity = GsonUtils.getObjFromJSON(getIntent().getStringExtra(Constants.INTENT_DATA), MonitorAddressEntity.class);
        setTitle(StringUtil.getDisplayName(monitorAddressEntity.name));
        txUtil = new TxUtil(this);
        ethTokenUtil = new EthTokenUtil(this);
        eosUtil = new EosBalanceUtil(this);
        eosTxUtil = new EosTxUtil(this);
        monitorAddressUtil = new MonitorAddressUtil(this);
        safeAssetUtil = new SafeAssetUtil(this);
        setRightImage(R.drawable.refresh_icon, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance();
            }
        });
        initBanner();
        initTxRecord();
        srLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        setAmount();
    }

    private void setAmount() {
        amoutTv.setText(BigDecimalUtils.formatBtc(monitorAddressEntity.ethAmount));
        if (monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) {
            SafeAssetTb item = safeAssetUtil.querySafeAssetTb(monitorAddressEntity.assetId);
            coinTv.setText(item.getAssetUnit());
        } else {
            coinTv.setText(StringUtil.getDisplayName(monitorAddressEntity.name));
        }
        bannerAdapter.notifyDataSetChanged();
    }

    private void updateBalance() {
        if (!Utils.isNetworkAvailable()) {
            ToastUtil.showShort(R.string.net_error);
            return;
        }
        switch (monitorAddressEntity.coinType) {
            case Constants.COIN_BTC:
            case Constants.COIN_SAFE_ASSET:
                MoreRequestDialogUtil.showProgressDialog(this);
                List<String> adddressList = monitorAddressUtil.queryUserCoinMonitorAddress(monitorAddressEntity.coin);
                UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(adddressList), monitorAddressEntity.coin, new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                        MoreRequestDialogUtil.dismissProgressDialog();
                        List<TxOutsTb> outsList = txUtil.getUnspendOuts(monitorAddressEntity.coin, "");
                        if (!TextUtils.isEmpty(monitorAddressEntity.assetId)) { //添加SAFE资产
                            outsList.addAll(txUtil.getUnspendOuts(monitorAddressEntity.coin, monitorAddressEntity.assetId));
                        }
                        List<CoinBalance> newCoinBalanceList = CreateCodeContentUtils.createBTCUpdateBanlanceContent(outsList);
                        qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), newCoinBalanceList);
                        if (newCoinBalanceList != null && newCoinBalanceList.size() > 0) {
                            monitorAddressEntity.ethAmount = txUtil.getAmountWithCoin(monitorAddressEntity.coin, monitorAddressEntity.assetId);
                            setAmount();
                        }
                        return null;
                    }
                });
                break;
            case Constants.COIN_ETC:
                MoreRequestDialogUtil.showProgressDialog(this);
                UpdateBanlanceUtil.getInstance().getEtcAddressInfo(monitorAddressUtil.queryUserETCMonitorAddress(), new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                        qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), coinBalanceList);
                        if (coinBalanceList != null && coinBalanceList.size() > 0) {
                            monitorAddressEntity.ethAmount = EthUtil.formatETHAmount(ethTokenUtil.getAmountWithAddress(monitorAddressUtil.queryUserETCMonitorAddress(), ""));
                            setAmount();
                        }
                        MoreRequestDialogUtil.dismissProgressDialog();
                        return null;
                    }

                });
                break;
            case Constants.COIN_ETH:
                MoreRequestDialogUtil.showProgressDialog(this);
                UpdateBanlanceUtil.getInstance().getEthAddressInfo(SafeGemApplication.getInstance().getCurrentETHAddress(), new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                        qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), coinBalanceList);
                        if (coinBalanceList != null && coinBalanceList.size() > 0) {
                            monitorAddressEntity.ethAmount = EthUtil.formatETHAmount(ethTokenUtil.getAmountWithAddress(SafeGemApplication.getInstance().getCurrentETHAddress(), ""));
                            setAmount();
                        }
                        MoreRequestDialogUtil.dismissProgressDialog();
                        return null;
                    }
                });
                break;
            case Constants.COIN_ETH_TOKEN:
                MoreRequestDialogUtil.showProgressDialog(this);
                List<String> address = new ArrayList<>();
                for (MonitorAddressTb monitorAddressTb : banerDatas) {
                    address.add(monitorAddressTb.getContractAddress());
                }
                UpdateBanlanceUtil.getInstance().getEthTokenAddressInfo(SafeGemApplication.getInstance().getCurrentETHAddress(), address, new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                        qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), coinBalanceList);
                        if (coinBalanceList != null && coinBalanceList.size() > 0) {
                            monitorAddressEntity.ethAmount = EthUtil.formatETHTokenAmount(ethTokenUtil.getAmountWithAddress(SafeGemApplication.getInstance().getCurrentETHAddress(), address.get(0)), Integer.parseInt(monitorAddressUtil.getDecimalsWithAddress(address.get(0))));
                            setAmount();
                        }
                        MoreRequestDialogUtil.dismissProgressDialog();
                        return null;
                    }
                });
                break;
            case Constants.COIN_EOS:
            case Constants.COIN_EOS_TOKEN:
                MoreRequestDialogUtil.showProgressDialog(this);
                UpdateBanlanceUtil.getInstance().getEosAddressInfo(SafeGemApplication.getInstance().getCurrentEOSAccount(), new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                        qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), coinBalanceList);
                        monitorAddressEntity.ethAmount = eosUtil.getAmountWithAddress(SafeGemApplication.getInstance().getCurrentEOSAccount(), monitorAddressEntity.name);
                        setAmount();
                        MoreRequestDialogUtil.dismissProgressDialog();
                        return null;
                    }
                });
                break;
            case Constants.COIN_USDT:
                MoreRequestDialogUtil.showProgressDialog(this);
                //先同步BTC，再同步USDT
                List<String> addressList = monitorAddressUtil.queryCurrentWalletMonitorAddress(Constants.BTC);
                UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(addressList), Constants.BTC, new OnUpdateBanlanceListener() {
                    @Override
                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> btcBalanceList) {
                        UpdateBanlanceUtil.getInstance().getUsdtAddressInfo(monitorAddressUtil.queryUserUsdtMonitorAddress(), new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> usdtBalanceList) {
                                List<CoinBalance> coinBalances = new ArrayList<>();
                                coinBalances.addAll(btcBalanceList);
                                coinBalances.addAll(usdtBalanceList);
                                qrCodeWindow = Utils.showQrCodeCotent(MonitorAddressDetailActivity.this, SafeGemApplication.getInstance().getColdUniqueId(), coinBalances);
                                MoreRequestDialogUtil.dismissProgressDialog();
                                return null;
                            }
                        });
                        return null;
                    }
                });
                break;
        }
    }

    @Override
    protected TransactionRecordPresenter createPresenter() {
        return new TransactionRecordPresenter(this);
    }

    private void initBanner() {
        banerDatas = monitorAddressEntity.monitorAddressList;
        final LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        bannerList.setLayoutManager(ms);
        bannerAdapter = new BaseRecyclerAdapter<MonitorAddressTb>(this, banerDatas, R.layout.monitor_grid_item) {

            @Override
            public void onViewDetachedFromWindow(BaseRecyclerHolder holder) {
                super.onViewDetachedFromWindow(holder);
            }

            @Override
            public void convert(BaseRecyclerHolder holder, MonitorAddressTb item, int position, boolean isScrolling) {
                holder.setText(R.id.addre_tv, item.getAddress()).setOnClickListener(R.id.rece_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(Constants.INTENT_DATA, GsonUtils.toString(banerDatas.get(position)));
                        map.put(Constants.INTENT_KEY1, GsonUtils.toString(monitorAddressEntity));
                        ActivitySkipUtil.skipAnotherActivity(MonitorAddressDetailActivity.this, ReceieveActivity.class, map);
                    }
                }).setOnClickListener(R.id.update_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateBalance();
                    }
                });
                String amount = "";
                switch (monitorAddressEntity.coinType) {
                    case Constants.COIN_BTC:
                        amount = BigDecimalUtils.formatBtc(txUtil.getAmountWithAddress(item.getAddress(), item.getCoin(), ""));
                        break;
                    case Constants.COIN_SAFE_ASSET:
                        amount = BigDecimalUtils.formatBtc(txUtil.getAmountWithAddress(item.getAddress(), item.getCoin(), monitorAddressEntity.assetId));
                        break;
                    case Constants.COIN_ETC:
                        amount = monitorAddressEntity.ethAmount;
                        break;
                    case Constants.COIN_ETH:
                        amount = monitorAddressEntity.ethAmount;
                        break;
                    case Constants.COIN_EOS:
                    case Constants.COIN_EOS_TOKEN:
                        amount = monitorAddressEntity.ethAmount;
                        break;
                    case Constants.COIN_ETH_TOKEN:
                        amount = monitorAddressEntity.ethAmount;
                        holder.setText(R.id.addre_tv, SafeGemApplication.getInstance().getCurrentETHAddress());
                        break;
                    case Constants.COIN_USDT:
                        amount = monitorAddressEntity.ethAmount;
                        break;

                }
                holder.setText(R.id.num, StringUtil.subZeroAndDot(amount));
            }
        };
        mGallerySnapHelper = new GallerySnapHelper();
        mGallerySnapHelper.attachToRecyclerView(bannerList);
        bannerList.setAdapter(bannerAdapter);
        //设置滚动监听
        bannerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滚动停止时，获取recycler显示的第一项的position
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstItemPosition = ms.findFirstVisibleItemPosition();
                    //高亮显示最左边的item
                    if (indicarView != null) {
                        indicarView.setCurIndicatorIndex(firstItemPosition);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        indicarView.setIndicatorCount(banerDatas.size());
    }

    private void initTxRecord() {
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, false));
        if (monitorAddressEntity.coinType == Constants.COIN_BTC || monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) {
            btcList = new ArrayList<>();
            btcAdapter = new CommonAdapter<TransactionRecordBean>(R.layout.tranc_record_item, btcList) {
                @Override
                protected void convert(ViewHolder viewHolder, TransactionRecordBean item, int position) {
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource(Utils.getCoinImg(item.coin, monitorAddressEntity.coinType));
                    TextView time = viewHolder.findViewById(R.id.time);
                    time.setText(item.time);
                    TextView tvCoin = viewHolder.findViewById(R.id.tv_coin);
                    if (monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) {
                        tvCoin.setText(monitorAddressEntity.name);
                    } else {
                        tvCoin.setText(StringUtil.getDisplayName(item.coin));
                    }
                    TextView tvAddress = viewHolder.findViewById(R.id.tv_address);
                    tvAddress.setText(item.address);
                    TextView tvAmount = viewHolder.findViewById(R.id.tv_amount);
                    String amount = BigDecimalUtils.formatBtc(item.amount);
                    if (BigDecimalUtils.greaterThan(amount, "0")) {
                        tvAmount.setTextColor(Utils.getColor(R.color.blue));
                        amount = "+" + amount;
                    } else {
                        tvAmount.setTextColor(Utils.getColor(R.color.text_color));
                    }
                    tvAmount.setText(StringUtil.subZeroAndDot(amount));
                }

                @Override
                protected void onItemClick(View view, TransactionRecordBean transactionRecordBean, int position) {
                    HashMap<String, Object> args = new HashMap<>(1);
                    args.put(Constants.INTENT_KEY1, btcList.get(position).txHash);
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, monitorAddressEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(MonitorAddressDetailActivity.this, TradeDetailActivity.class, args);
                }
            };
            rvList.setAdapter(btcAdapter);
            List<String> adddressList = monitorAddressUtil.queryUserCoinMonitorAddress(monitorAddressEntity.coin);
            MoreRequestDialogUtil.showProgressDialog(this);
            UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(adddressList), monitorAddressEntity.coin, new OnUpdateBanlanceListener() {
                @Override
                public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                    mPresenter.getBtcLocalTransaction(monitorAddressEntity.coin, monitorAddressEntity.assetId, pageIndex);
                    MoreRequestDialogUtil.dismissProgressDialog();
                    return null;
                }
            });
        } else if (monitorAddressEntity.coinType == Constants.COIN_EOS || monitorAddressEntity.coinType == Constants.COIN_EOS_TOKEN) {
            eosList = new ArrayList<>();
            eosAdapter = new CommonAdapter<EosTxTb>(R.layout.tranc_record_item, eosList) {
                @Override
                protected void convert(ViewHolder viewHolder, EosTxTb item, int i) {
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource(Utils.getCoinImg(item.getCoin(), monitorAddressEntity.coinType));
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
                    args.put(Constants.INTENT_KEY1, eosList.get(position).getTxId());
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, monitorAddressEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(MonitorAddressDetailActivity.this, TradeDetailActivity.class, args);
                }
            };
            rvList.setAdapter(eosAdapter);
            String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
            Long position = eosTxUtil.queryEosTxCount(account);
            if (position > 0L) {
                position++;
            }
            mPresenter.getEosTransactionRecord(account, position);
            mPresenter.getEosLocalTransactionRecord(account, monitorAddressEntity.name, pageIndex);

        } else if (monitorAddressEntity.coinType == Constants.COIN_USDT) {
            usdtList = new ArrayList<>();
            String usdtAddress = SafeGemApplication.getInstance().getCurrentUSDTAddress();
            usdtAdapter = new CommonAdapter<UsdtTxTb>(R.layout.tranc_record_item, usdtList) {
                @Override
                protected void convert(ViewHolder viewHolder, UsdtTxTb item, int i) {
                    ImageView img = viewHolder.findViewById(R.id.img);
                    img.setImageResource(Utils.getCoinImg(Constants.USDT, Constants.COIN_USDT));
                    TextView time = viewHolder.findViewById(R.id.time);
                    time.setText(item.getFormatTime());
                    TextView tvCoin = viewHolder.findViewById(R.id.tv_coin);
                    tvCoin.setText(Constants.USDT);
                    TextView tvAddress = viewHolder.findViewById(R.id.tv_address);
                    TextView tvAmount = viewHolder.findViewById(R.id.tv_amount);
                    tvAddress.setText(item.getReferenceaddress());
                    if (item.getSendingaddress().equals(usdtAddress)) {
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
                    args.put(Constants.INTENT_KEY1, usdtList.get(position).getTxid());
                    args.put(Constants.INTENT_KEY2, SafeGemApplication.getInstance().getColdUniqueId());
                    args.put(Constants.INTENT_KEY3, monitorAddressEntity.coinType);
                    ActivitySkipUtil.skipAnotherActivity(MonitorAddressDetailActivity.this, TradeDetailActivity.class, args);
                }
            };
            rvList.setAdapter(usdtAdapter);
            String address = SafeGemApplication.getInstance().getCurrentUSDTAddress();
            mPresenter.getUsdtTransactionRecord(address);
            mPresenter.getUsdtLocalTransactionRecord(pageIndex);
        }

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                if (monitorAddressEntity.coinType == Constants.COIN_BTC || monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
                    mPresenter.getBtcLocalTransaction(monitorAddressEntity.coin, monitorAddressEntity.assetId, pageIndex);
                } else if (monitorAddressEntity.coinType == Constants.COIN_USDT) { // USDT
                    mPresenter.getUsdtLocalTransactionRecord(pageIndex);
                } else if(monitorAddressEntity.coinType == Constants.COIN_EOS || monitorAddressEntity.coinType == Constants.COIN_EOS_TOKEN){
                    String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
                    mPresenter.getEosLocalTransactionRecord(account, monitorAddressEntity.name, pageIndex);
                }
            }
        });
        rvList.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                if (monitorAddressEntity.coinType == Constants.COIN_BTC || monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
                    mPresenter.getBtcLocalTransaction(monitorAddressEntity.coin, monitorAddressEntity.assetId, pageIndex);
                } else if (monitorAddressEntity.coinType == Constants.COIN_USDT) { // USDT
                    mPresenter.getUsdtLocalTransactionRecord(pageIndex);
                } else if(monitorAddressEntity.coinType == Constants.COIN_EOS || monitorAddressEntity.coinType == Constants.COIN_EOS_TOKEN){
                    String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
                    mPresenter.getEosLocalTransactionRecord(account, monitorAddressEntity.name, pageIndex);
                }
            }
        });
    }

    public void refreshUI() {
        if (monitorAddressEntity.coinType == Constants.COIN_BTC || monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) {
            if (btcList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            btcAdapter.notifyDataSetChanged();
        } else if (monitorAddressEntity.coinType == Constants.COIN_USDT) {
            if (usdtList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            usdtAdapter.notifyDataSetChanged();
        } else if(monitorAddressEntity.coinType == Constants.COIN_EOS || monitorAddressEntity.coinType == Constants.COIN_EOS_TOKEN){
            if (eosList.size() == 0) {
                rvList.setVisibility(View.GONE);
                tvNullData.setVisibility(View.VISIBLE);
            } else {
                rvList.setVisibility(View.VISIBLE);
                tvNullData.setVisibility(View.GONE);
            }
            eosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetTransactionRecord(List<TransactionRecordBean> recordBeans) {
        if (pageIndex == 1) {
            btcList.clear();
            btcList.addAll(recordBeans);
        } else {
            btcList.addAll(recordBeans);
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
            eosList.clear();
            eosList.addAll(recordBeans);
        } else {
            eosList.addAll(recordBeans);
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
            usdtList.clear();
            usdtList.addAll(recordBeans);
        } else {
            usdtList.addAll(recordBeans);
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
        if (monitorAddressEntity.coinType == Constants.COIN_BTC || monitorAddressEntity.coinType == Constants.COIN_SAFE_ASSET) { // BTC
            btcList.clear();
            pageIndex = 1;
            mPresenter.getBtcLocalTransaction(monitorAddressEntity.coin, monitorAddressEntity.assetId, pageIndex);
        } else if (monitorAddressEntity.coinType == Constants.COIN_USDT) { // USDT
            usdtList.clear();
            pageIndex = 1;
            mPresenter.getUsdtLocalTransactionRecord(pageIndex);
        } else if(monitorAddressEntity.coinType == Constants.COIN_EOS || monitorAddressEntity.coinType == Constants.COIN_EOS_TOKEN){ // EOS
            eosList.clear();
            pageIndex = 1;
            String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
            mPresenter.getEosLocalTransactionRecord(account, monitorAddressEntity.name, pageIndex);
        }
    }

    @Override
    public void onBackPressed() {
        if (qrCodeWindow != null && qrCodeWindow.getPopupWindow() != null && qrCodeWindow.getPopupWindow().isShowing()) {
            qrCodeWindow.getPopupWindow().dismiss();
            qrCodeWindow = null;
            return;
        }
        super.onBackPressed();
    }

}
