package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bankledger.protobuf.bean.Address;
import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.protobuf.bean.TransAddress;
import com.bankledger.protobuf.bean.TransEos;
import com.bankledger.protobuf.bean.TransMulAddress;
import com.bankledger.protobuf.bean.TransSignTx;
import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.MainItemBean;
import com.bankledger.safegem.greendaodb.ColdWalletUtil;
import com.bankledger.safegem.greendaodb.DaoManager;
import com.bankledger.safegem.greendaodb.MessageUtils;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.greendaodb.entity.MessageTb;
import com.bankledger.safegem.listener.OnUpdateBanlanceListener;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.model.request.AddColdWalletRequest;
import com.bankledger.safegem.net.model.request.SendTransactionRequest;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;
import com.bankledger.safegem.presenter.MainPresenter;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.scan.ScanActivity;
import com.bankledger.safegem.service.UpdateBalanceService;
import com.bankledger.safegem.ui.view.ChatDetailItemDecoration;
import com.bankledger.safegem.ui.view.DividerGridItemDecoration;
import com.bankledger.safegem.ui.view.GallerySnapHelper;
import com.bankledger.safegem.ui.view.IndicatorView;
import com.bankledger.safegem.ui.view.NoScollGridLayoutManager;
import com.bankledger.safegem.ui.view.QRCodeWindow;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.BitmapUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.MoreRequestDialogUtil;
import com.bankledger.safegem.utils.RingManager;
import com.bankledger.safegem.utils.SharedPreferencesUtil;
import com.bankledger.safegem.utils.StatusBarUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.UpdateBanlanceUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.IMainView;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;


public class MainActivity extends MVPBaseActivity<IMainView, MainPresenter> implements IMainView {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mFunctionRecyclerView;

    @BindView(R.id.banner_recyclerview)
    RecyclerView bannerRecyclerview;

    @BindView(R.id.right)
    LinearLayout right;

    @BindView(R.id.left)
    LinearLayout left;

    @BindView(R.id.ind_center_img)
    ImageView indCenterImg;

    @BindView(R.id.phone_tv)
    TextView phoneTv;

    @BindView(R.id.id_tv)
    TextView idTv;

    @BindView(R.id.wallet_name)
    TextView wallet_name;

    @BindView(R.id.new_tv1)
    TextView newTv1;

    @BindView(R.id.new_tv2)
    TextView newTv2;

    @BindView(R.id.red_dot)
    ImageView redDot;

    @BindView(R.id.time_tv1)
    TextView timeTv1;

    @BindView(R.id.time_tv2)
    TextView timeTv2;

    @BindView(R.id.indicar_view)
    IndicatorView indicarView;

    private List<MainItemBean> mFunctionDatas;
    private BaseRecyclerAdapter<MainItemBean> mFunctionAdapter;
    private List<ColdWalletTb> mBannertionDatas;
    private BaseRecyclerAdapter<ColdWalletTb> mBannerAdapter;
    private GallerySnapHelper mGallerySnapHelper;
    private int currentPosition = 0;
    private ColdWalletUtil coldWalletUtil;
    private Intent updateBalanceService;
    private MessageUtils messageUtils;
    private MonitorAddressUtil monitorAddressUtil = new MonitorAddressUtil(this);

    private String result;
    private MsgReceiver msgReceiver;
    private QRCodeWindow qrCodeWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        drawerLayout.addDrawerListener(mSimpleDrawerListener);
        coldWalletUtil = new ColdWalletUtil(this);
        messageUtils = new MessageUtils(this);
        initCard();
        initFunction();
        //读写权限
        String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        getActivity().verifyPermissions(PERMISSIONS_STORAGE, new PermissionCallBack() {
            @Override
            public void onGranted() {
                updateBalanceService = new Intent(MainActivity.this, UpdateBalanceService.class);
                startService(updateBalanceService);
                mPresenter.getAppVersion(Utils.getAppVersionCode(MainActivity.this), Constants.HOT_PRODUCT_TYPE);
            }

            @Override
            public void onDenied() {

            }
        });

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getPackageName() + Constants.RECEIVER);
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (safeGemApplication.getUserInfo().getPicUrl() != null) {
            BitmapUtil.loadImage(safeGemApplication.getUserInfo().getPicUrl(), indCenterImg, R.drawable.head_default);
        }
        wallet_name.setText(safeGemApplication.getUserInfo().getNickName());
    }

    private void updateMessageView() {
        if (safeGemApplication.isUserNotNull() && safeGemApplication.getUserId() != null) {
            setTextContent();
            if (redDot != null) {
                if (redDot.getVisibility() == View.GONE) {
                    redDot.setVisibility(View.VISIBLE);
                    RingManager.getInstance().playCrystal();
                }
            }
        }
    }

    private void setTextContent() {
        List<MessageTb> msgList = messageUtils.queryFirstMessage();
        for (int i = 0; i < msgList.size(); i++) {
            try {
                MessageTb item = msgList.get(i);
                if (i == 0) {
                    if (item.msgType == Constants.RECEIVE_MSG_TYPE) {
                        newTv1.setText("." + getString(R.string.receive_broad));
                    } else if (item.msgType == Constants.SEND_MSG_TYPE) {
                        newTv1.setText("." + getString(R.string.send_broad));
                    } else {
                        newTv1.setText("." + msgList.get(i).title);
                    }
                    timeTv1.setText(DateTimeUtil.getShortDateTimeString(DateTimeUtil.getDateTimeForTimeZone(msgList.get(i).date)));
                } else {
                    if (item.msgType == Constants.RECEIVE_MSG_TYPE) {
                        newTv2.setText("." + getString(R.string.receive_broad));
                    } else if (item.msgType == Constants.SEND_MSG_TYPE) {
                        newTv2.setText("." + getString(R.string.send_broad));
                    } else {
                        newTv2.setText("." + msgList.get(i).title);
                    }
                    timeTv2.setText(DateTimeUtil.getShortDateTimeString(DateTimeUtil.getDateTimeForTimeZone(msgList.get(i).date)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initView() {
        super.initView();
        if (safeGemApplication.getUserInfo() != null && safeGemApplication.getUserInfo().getIncrementId() != null) {
            idTv.setText(safeGemApplication.getUserInfo().getIncrementId() + "");
        }
        if (safeGemApplication.getUserInfo() != null && safeGemApplication.getUserInfo().getPhone().contains(Constants.RIGHT_BRACKET)) {
            phoneTv.setText(safeGemApplication.getUserInfo().getPhone().substring(safeGemApplication.getUserInfo().getPhone().indexOf(Constants.RIGHT_BRACKET) + 1));
        }
    }

    private void initCard() {
        mBannertionDatas = coldWalletUtil.queryAllWallet();
        if (mBannertionDatas.size() == 0) {
            mBannertionDatas.add(null);
            newTv1.setText(R.string.user_wallet_tip);
            newTv2.setText(R.string.please_add_wallet_tip);
        }
        final LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        bannerRecyclerview.setLayoutManager(ms);
        bannerRecyclerview.addItemDecoration(new ChatDetailItemDecoration(20));
        mBannerAdapter = new BaseRecyclerAdapter<ColdWalletTb>(this, mBannertionDatas, 0) {
            @Override
            public int getItemViewType(int position) {
                ColdWalletTb walletTb = list.get(position);
                if (walletTb == null) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                if (viewType == 0) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_wallet_item_layout, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_item_layout, parent, false);
                }
                return BaseRecyclerHolder.getRecyclerHolder(parent.getContext(), view);
            }

            @Override
            public void convert(BaseRecyclerHolder holder, ColdWalletTb item, int position, boolean isScrolling) {
                if (item == null) {
                    holder.setOnClickListener(R.id.card, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                                @Override
                                public void onGranted() {
                                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                    intent.putExtra(Constants.INTENT_KEY1, getString(R.string.add_walet));
                                    intent.putExtra(Constants.INTENT_KEY2, getString(R.string.add_wallet_tip));
                                    intent.putExtra(Constants.SCAN_FLAG, Constants.ADDWALLET);
                                    startActivityForResult(intent, Constants.REQUEST_CODE);
                                }

                                @Override
                                public void onDenied() {

                                }
                            });
                        }
                    });
                } else {
                    String formatColdId = item.getColdUniqueId().substring(item.getColdUniqueId().length() - 8);
                    holder.setText(R.id.name, item.getColdWalletName());
                    holder.setText(R.id.num_tv, formatColdId);
                    holder.setOnClickListener(R.id.rece_view, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (monitorAddressUtil.queryUserCoinMonitorAddressCount() > 0) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Constants.WALLET_ID_DATA, item.getColdUniqueId());
                                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, ReceieveActivity.class, map);
                            } else {
                                ToastUtil.showShort(getActivity(), getString(R.string.monitor_tip_content));
                            }
                        }
                    }).setOnClickListener(R.id.update_view, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Utils.isRepeatClick()) {
                                if (monitorAddressUtil.queryUserCoinMonitorAddressCount() > 0) {
                                    MoreRequestDialogUtil.showProgressDialog(MainActivity.this);
                                    UpdateBanlanceUtil.getInstance().updateWalletBalance(item.getColdUniqueId(), new OnUpdateBanlanceListener() {
                                        @Override
                                        public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                                            qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, item.getColdUniqueId(), coinBalanceList);
                                            MoreRequestDialogUtil.dismissProgressDialog();
                                            return null;
                                        }
                                    });
                                } else {
                                    ToastUtil.showShort(getActivity(), getString(R.string.monitor_tip_content));
                                }
                            }
                        }
                    });
                }
            }
        };
        mBannerAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position) {
                if (mBannertionDatas.get(position) != null) {
                    DialogUtil.showTextDialog(MainActivity.this, R.string.warning_tip, R.string.remove_wallet, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String coldUniqueId = mBannertionDatas.get(position).getColdUniqueId();
                            mPresenter.removeColdWallet(coldUniqueId, position);
                        }
                    });
                }
                return false;
            }
        });
        mGallerySnapHelper = new GallerySnapHelper();
        mGallerySnapHelper.attachToRecyclerView(bannerRecyclerview);
        bannerRecyclerview.setAdapter(mBannerAdapter);
        mBannerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if (mBannertionDatas.get(position) != null) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(Constants.INTENT_DATA, mBannertionDatas.get(position).getColdWalletName());
                    ActivitySkipUtil.skipAnotherActivity(MainActivity.this, MonitorAddressActivity.class, map);
                }
            }
        });

        //设置滚动监听
        bannerRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滚动停止时，获取recycler显示的第一项的position
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPosition = ms.findFirstVisibleItemPosition();
                    ColdWalletTb coldWallet = mBannertionDatas.get(currentPosition);
                    if (coldWallet != null) {
                        SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.CURRENT_WALLET_ID, coldWallet.getColdUniqueId());
                        SafeGemApplication.getInstance().appSharedPrefUtil.putInt(Constants.CURRENT_WALLET_POSITION, currentPosition);
                    }
                    indicarView.setCurIndicatorIndex(currentPosition);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        currentPosition = SharedPreferencesUtil.getDefaultPreferences(this).getInt(Constants.CURRENT_WALLET_POSITION, -1);
        if (currentPosition != -1) {
            bannerRecyclerview.smoothScrollToPosition(currentPosition);
        }
        indicarView.setIndicatorCount(mBannertionDatas.size());
    }

    private void initFunction() {
        mFunctionDatas = new ArrayList<>();
        mPresenter.getMainData(this);
        mFunctionAdapter = new BaseRecyclerAdapter<MainItemBean>(this, mFunctionDatas, R.layout.main_item_layout) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void convert(BaseRecyclerHolder holder, MainItemBean item, int position, boolean isScrolling) {
                int count = position / 4;
                if (count % 2 == 0) {
                    holder.itemView.setBackgroundColor(position % 2 == 0 ? Utils.getColor(R.color.item_bg_color) : Utils.getColor(R.color.white));
                } else {
                    holder.itemView.setBackgroundColor(position % 2 == 1 ? Utils.getColor(R.color.item_bg_color) : Utils.getColor(R.color.white));
                }
                holder.setText(R.id.name_tv, item.getMainName()).setImageResource(R.id.sign_img, item.getImgId());
            }
        };
        mFunctionRecyclerView.setLayoutManager(new NoScollGridLayoutManager(this, 4));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFunctionRecyclerView.addItemDecoration(new DividerGridItemDecoration(1, getColor(R.color.divder_color)));
        } else {
            mFunctionRecyclerView.addItemDecoration(new DividerGridItemDecoration(1, getResources().getColor(R.color.divder_color)));
        }
        //解决数据加载不完的问题
        mFunctionRecyclerView.setNestedScrollingEnabled(false);
        mFunctionRecyclerView.setHasFixedSize(true);
        //解决数据加载完成后, 没有停留在顶部的问题
        mFunctionRecyclerView.setFocusable(false);
        mFunctionRecyclerView.setAdapter(mFunctionAdapter);
        mFunctionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                if (Utils.isRepeatClick()) {
                    Class<? extends Activity> cls = mFunctionDatas.get(position).getCls();
                    if (cls != null) {
                        ActivitySkipUtil.skipAnotherActivity(MainActivity.this, cls);
                    } else if (position == 6) { //EOS管理
                        List<ColdWalletTb> mWalletList = coldWalletUtil.queryAllWallet();
                        if (mWalletList == null || mWalletList.size() == 0) {
                            ToastUtil.showShort(getActivity(), R.string.please_add_wallet);
                        } else {
                            if (TextUtils.isEmpty(SafeGemApplication.getInstance().getCurrentEOSAccount())) {
                                getActivity().verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                                    @Override
                                    public void onGranted() {
                                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                        intent.putExtra(Constants.INTENT_KEY2, getString(R.string.create_eos_tips));
                                        intent.putExtra(Constants.SCAN_FLAG, Constants.EOS_SCAN);
                                        startActivityForResult(intent, Constants.REQUEST_CODE);
                                    }

                                    @Override
                                    public void onDenied() {
                                    }
                                });
                            } else {
                                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, EosAccountActivity.class);
                            }
                        }
                    } else if (position == 7) { //EOS签名
                        mPresenter.getEosSignParam();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.TYPE_SEND_TX:
                TransSignTx transSignTx = (TransSignTx) data.getSerializableExtra(Constants.SCAN_DATA);
                List<ColdWalletTb> mWalletList = coldWalletUtil.queryAllWallet();
                ColdWalletTb selectWallet = null;
                for (ColdWalletTb item : mWalletList) {
                    if (item.getColdUniqueId().equals(transSignTx.walletSeqNumber)) {
                        selectWallet = item;
                        break;
                    }
                }
                if (selectWallet == null) {
                    ToastUtil.showShort(getActivity(), R.string.please_go_wallet_send_tx);
                    return;
                }
                SendTransactionRequest sendTransactionRequest = QRCodeUtil.decodeSendTrade(transSignTx);
                mPresenter.sendTx(sendTransactionRequest);
                break;
            case Constants.TYPE_ADDR:
                TransAddress transAddress = (TransAddress) data.getSerializableExtra(Constants.SCAN_DATA);
                mWalletList = coldWalletUtil.queryAllWallet();
                selectWallet = null;
                for (ColdWalletTb item : mWalletList) {
                    if (item.getColdUniqueId().equals(transAddress.walletSeqNumber)) {
                        selectWallet = item;
                        break;
                    }
                }
                if (selectWallet == null) {
                    ToastUtil.showShort(getActivity(), R.string.please_add_wallet);
                    return;
                }
                Address addressBean = transAddress.address;
                switch (addressBean.coinType) {
                    case Constants.COIN_BTC:
                        List<String> addressList = new ArrayList<>();
                        addressList.add(addressBean.address);
                        MoreRequestDialogUtil.showProgressDialog(this);
                        UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(addressList), addressBean.coin, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                                qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, transAddress.walletSeqNumber, coinBalanceList);
                                MoreRequestDialogUtil.dismissProgressDialog();
                                return null;
                            }
                        });
                        break;
                    case Constants.COIN_ETH:
                        MoreRequestDialogUtil.showProgressDialog(this);
                        UpdateBanlanceUtil.getInstance().getEthAddressInfo(addressBean.address, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                                qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, transAddress.walletSeqNumber, coinBalanceList);
                                MoreRequestDialogUtil.dismissProgressDialog();
                                return null;
                            }
                        });
                        break;
                    case Constants.COIN_ETH_TOKEN:
                        List<String> list = new ArrayList<>();
                        list.add(addressBean.contractsAddress);
                        MoreRequestDialogUtil.showProgressDialog(this);
                        UpdateBanlanceUtil.getInstance().getEthTokenAddressInfo(addressBean.address, list, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                                qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, transAddress.walletSeqNumber, coinBalanceList);
                                MoreRequestDialogUtil.dismissProgressDialog();
                                return null;
                            }
                        });
                        break;
                    case Constants.COIN_ETC:
                        MoreRequestDialogUtil.showProgressDialog(this);
                        UpdateBanlanceUtil.getInstance().getEtcAddressInfo(addressBean.address, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> coinBalanceList) {
                                qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, transAddress.walletSeqNumber, coinBalanceList);
                                MoreRequestDialogUtil.dismissProgressDialog();
                                return null;
                            }
                        });
                        break;
                    case Constants.COIN_USDT:
                        MoreRequestDialogUtil.showProgressDialog(this);
                        List<String> btcAddressList = monitorAddressUtil.queryCurrentWalletMonitorAddress(Constants.BTC);
                        UpdateBanlanceUtil.getInstance().merageTransactionList(Utils.getAddressMessageList(btcAddressList), Constants.BTC, new OnUpdateBanlanceListener() {
                            @Override
                            public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> btcBalanceList) {
                                UpdateBanlanceUtil.getInstance().getUsdtAddressInfo(addressBean.address, new OnUpdateBanlanceListener() {
                                    @Override
                                    public ObservableSource<List<CoinBalance>> onUpdateSuccess(List<CoinBalance> usdtBalanceList) {
                                        List<CoinBalance> coinBalances = new ArrayList<>();
                                        coinBalances.addAll(btcBalanceList);
                                        coinBalances.addAll(usdtBalanceList);
                                        qrCodeWindow = Utils.showQrCodeCotent(MainActivity.this, transAddress.walletSeqNumber, coinBalances);
                                        MoreRequestDialogUtil.dismissProgressDialog();
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        break;
                    default:
                        break;
                }
                break;
            case Constants.TYPE_MON:
                if (mBannertionDatas.size() == 1 && mBannertionDatas.get(0) == null) {
                    ToastUtil.showShort(getActivity(), getString(R.string.please_add_wallet));
                } else {
                    mPresenter.addColdMonitorAddress((TransMulAddress) data.getSerializableExtra(Constants.SCAN_DATA));
                }
                break;
            case Constants.TYPE_ADDWALLET:
                mPresenter.addColdWallet((AddColdWalletRequest) data.getSerializableExtra(Constants.SCAN_DATA));
                break;
            case Constants.TYPE_EOS:
                TransEos transEos = (TransEos) data.getSerializableExtra(Constants.SCAN_DATA);
                mWalletList = coldWalletUtil.queryAllWallet();
                selectWallet = null;
                for (ColdWalletTb item : mWalletList) {
                    if (item.getColdUniqueId().equals(transEos.walletSeqNumber)) {
                        selectWallet = item;
                        break;
                    }
                }
                if (selectWallet == null) {
                    ToastUtil.showShort(getActivity(), R.string.please_add_wallet);
                    return;
                }
                if (TextUtils.isEmpty(SafeGemApplication.getInstance().getCurrentEOSAccount())) {
                    Intent intent = new Intent(MainActivity.this, EosCreateActivity.class);
                    intent.putExtra(Constants.INTENT_KEY1, transEos);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(getActivity(), R.string.eos_has_account);
                    ActivitySkipUtil.skipAnotherActivity(MainActivity.this, EosAccountActivity.class);
                }
                break;
            case Constants.TYPE_UPDATE_WALLET:
                result = data.getStringExtra(Constants.SCAN_DATA);
                mPresenter.getAppVersion(Utils.getColdVersion(result), Constants.COLD_PRODUCT_TYPE);
                break;
            case Constants.TYPE_LANGUAGE:
                ToastUtil.clearToast();
                recreate();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (drawerLayout != null) {
            drawerLayout.removeDrawerListener(mSimpleDrawerListener);
        }
        stopService(updateBalanceService);
        RetrofitManager.clearInstance();
        DaoManager.getInstance().closeConnection();
        UpdateBanlanceUtil.clearInstance();
        unregisterReceiver(msgReceiver);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void onMainDataComplete(List<MainItemBean> datas) {
        mFunctionDatas.addAll(datas);
    }

    @Override
    public void onSendTxComplete(String hex) {
        setTextContent();
        ToastUtil.showShort(this, getString(R.string.send_success));
    }

    @Override
    public void onShowDialog(String msg) {
        DialogUtil.showTextDialog(this, getString(R.string.hint_str), msg, null);
    }

    @Override
    public void onAddColdWalletComplete(ColdWalletTb coldWallet) {
        if (mBannertionDatas.size() < 5) {
            if (mBannertionDatas.size() == 1 && mBannertionDatas.get(0) == null) {
                mBannertionDatas.remove(0);
            }
            mBannertionDatas.add(coldWallet);
            currentPosition = mBannertionDatas.size() - 1;
            changWallet();
        }
    }

    @Override
    public void onRemoveColdWalletComplete(int position) {
        mBannertionDatas.remove(position);
        if (mBannertionDatas.size() == 0) {
            mBannertionDatas.add(null);
        }
        currentPosition = 0;
        changWallet();
        ToastUtil.showShort(getActivity(), getString(R.string.delet_wallet_success));
    }

    private void changWallet() {
        mBannerAdapter.notifyDataSetChanged();
        indicarView.setIndicatorCount(mBannertionDatas.size());
        indicarView.setCurIndicatorIndex(currentPosition);
        bannerRecyclerview.smoothScrollToPosition(currentPosition);
        if (mBannertionDatas.get(currentPosition) != null) {
            SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.CURRENT_WALLET_ID, mBannertionDatas.get(currentPosition).getColdUniqueId());
        }
        SafeGemApplication.getInstance().appSharedPrefUtil.putInt(Constants.CURRENT_WALLET_POSITION, currentPosition);
    }

    @Override
    public void onUpdateColdWalletComplete(String id, String name) {
        for (ColdWalletTb coldWalletTb : mBannertionDatas) {
            if (coldWalletTb.getColdUniqueId().equals(id)) {
                coldWalletTb.setColdWalletName(name);
                break;
            }
        }
        mBannerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetMessageComplete() {
        setTextContent();
    }

    private ProgressDialog progressDialog;

    @Override
    public void onHasNewVersion(GetAppVersionResponse getAppVersionResponse, int productType) {
        if (productType == Constants.HOT_PRODUCT_TYPE) {
            //显示提示更新对话框
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.update_version_tips)
                    .setMessage(Html.fromHtml(getAppVersionResponse.updateDesc))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setTitle(getString(R.string.dowload_tip));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.setMax(100);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.show();
                            mPresenter.downloadApk(getAppVersionResponse.downUrl);
                        }
                    }).create().show();
        } else {
            Intent intent = new Intent(this, AppUpgradeActivity.class);
            intent.putExtra(Constants.INTENT_KEY1, result);
            intent.putExtra(Constants.INTENT_KEY2, getAppVersionResponse);
            startActivity(intent);
        }

    }

    @Override
    public void onNoNewVersion(int productType) {
        if (productType == Constants.HOT_PRODUCT_TYPE) {
            mPresenter.getMsgList();
        } else {
            ToastUtil.showShort(getActivity(), getString(R.string.cold_wallet_already_lastest_version));
        }

    }

    @Override
    public void onDownloadSuccess(File file) {
        Utils.installApp(file, getPackageName() + Constants.provider, this);
    }

    @Override
    public void onDownloadFail(Throwable throwable) {
        progressDialog.dismiss();
        ToastUtil.showShort(getActivity(), getString(R.string.update_fail));
    }

    @Override
    public void onProgress(int progress, long total) {
        progressDialog.setProgress(progress);
    }

    @Override
    public void onGetSingComplete(String singContent) {
        qrCodeWindow = new QRCodeWindow(this, singContent);
        qrCodeWindow.show(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.header, R.id.btn_scan, R.id.about_img, R.id.ind_center_img, R.id.help_img, R.id.set_view, R.id.message_view, R.id.buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.header:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.btn_scan:
                getActivity().verifyPermissions(Manifest.permission.CAMERA, new PermissionCallBack() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        intent.putExtra(Constants.SCAN_FLAG, Constants.ALL_SCAN);
                        startActivityForResult(intent, Constants.REQUEST_CODE);
                    }

                    @Override
                    public void onDenied() {
                    }
                });
                break;
            case R.id.about_img:
                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, AboutUsActivity.class);
                break;
            case R.id.ind_center_img:
                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, IndividualCenterActivity.class);
                break;
            case R.id.help_img:
                ToastUtil.showShort(getActivity(), R.string.please_anwang_tip);
//                HashMap<String, String> helep_map = new HashMap<>();
//                helep_map.put(Constants.INTENT_DATA, getString(R.string.help_str));
//                helep_map.put(Constants.URL, Constants.ANBAO_URL);
//                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, WebviewActivity.class, helep_map);
                break;
            case R.id.buy:
                ToastUtil.showShort(getActivity(), R.string.please_anwang_tip);
//                HashMap<String, String> buy_map = new HashMap<>();
//                buy_map.put(Constants.INTENT_DATA, getString(R.string.buy_wallet_str));
//                buy_map.put(Constants.URL, Constants.ANBAO_URL);
//                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, WebviewActivity.class, buy_map);
                break;
            case R.id.set_view:
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), 1);
                break;
            case R.id.message_view:
                SafeGemApplication.getInstance().setLastMsgId(messageUtils.queryLastMsgId());
                redDot.setVisibility(View.GONE);
                ActivitySkipUtil.skipAnotherActivity(MainActivity.this, MessageCenterActivity.class);
                break;

        }
    }

    private DrawerLayout.SimpleDrawerListener mSimpleDrawerListener = new DrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerOpened(View drawerView) {
            //档DrawerLayout打开时，让整体DrawerLayout布局可以响应点击事件
            drawerView.setClickable(true);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            right.layout(left.getRight(), 0, left.getRight() + display.getWidth(), display.getHeight());
            super.onDrawerSlide(drawerView, slideOffset);
        }
    };

    /**
     * 广播接收器
     */
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            int message = intent.getIntExtra(Constants.INTENT_DATA, -1);
            if (message == Constants.NEW_COIN) {
                updateMessageView();
            } else if (message == Constants.SHOW_DATA) {
                onGetMessageComplete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (qrCodeWindow != null && qrCodeWindow.getPopupWindow().isShowing()) {
            qrCodeWindow.getPopupWindow().dismiss();
            qrCodeWindow = null;
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
