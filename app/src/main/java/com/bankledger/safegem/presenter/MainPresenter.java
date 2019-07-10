package com.bankledger.safegem.presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bankledger.protobuf.bean.Address;
import com.bankledger.protobuf.bean.TransMulAddress;
import com.bankledger.protobuf.bean.TransSignParam;
import com.bankledger.protobuf.utils.ProtoUtils;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.MainItemBean;
import com.bankledger.safegem.greendaodb.ColdWalletUtil;
import com.bankledger.safegem.greendaodb.EosAccountUtil;
import com.bankledger.safegem.greendaodb.EosBalanceUtil;
import com.bankledger.safegem.greendaodb.EthTokenUtil;
import com.bankledger.safegem.greendaodb.MessageUtils;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.TxUtil;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.MessageTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.download.DownloadApiManager;
import com.bankledger.safegem.net.download.DownloadClient;
import com.bankledger.safegem.net.download.FileDownLoadObserver;
import com.bankledger.safegem.net.model.request.AddColdMonitorAddressRequest;
import com.bankledger.safegem.net.model.request.AddColdWalletRequest;
import com.bankledger.safegem.net.model.request.CheckEosAccountRequest;
import com.bankledger.safegem.net.model.request.GetAppVersionRequest;
import com.bankledger.safegem.net.model.request.RemoveColdWalletRequest;
import com.bankledger.safegem.net.model.request.SendTransactionRequest;
import com.bankledger.safegem.net.model.response.AddColdMonitorAddressResponse;
import com.bankledger.safegem.net.model.response.AddColdWalletResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.BtcSendTransactionResponse;
import com.bankledger.safegem.net.model.response.CheckEosAccountResponse;
import com.bankledger.safegem.net.model.response.EthSendTransationResponse;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;
import com.bankledger.safegem.net.model.response.GetEosSignResponse;
import com.bankledger.safegem.net.model.response.GetMsgListResponse;
import com.bankledger.safegem.ui.activity.AddressBookActivity;
import com.bankledger.safegem.ui.activity.BestFeeActivity;
import com.bankledger.safegem.ui.activity.CreateQrcodeActivity;
import com.bankledger.safegem.ui.activity.ERCActivity;
import com.bankledger.safegem.ui.activity.TimeAdjustActivity;
import com.bankledger.safegem.ui.activity.TransactionCoinActivity;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.RingManager;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.view.IMainView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Date：2018/7/30
 * Author: bankledger
 */
public class MainPresenter extends BasePresenter<IMainView> {

    private Context mContext;
    private TxUtil txUtil;
    private MessageUtils messageUtils;
    private MonitorAddressUtil monitorAddressUtil;
    private ColdWalletUtil coldWalletUtil;
    private EthTokenUtil ethTokenUtil;
    private EosBalanceUtil eosTbUtil;


    public MainPresenter(Context mContext) {
        this.mContext = mContext;
        txUtil = new TxUtil(mContext);
        messageUtils = new MessageUtils(mContext);
        monitorAddressUtil = new MonitorAddressUtil(mContext);
        coldWalletUtil = new ColdWalletUtil(mContext);
        eosTbUtil = new EosBalanceUtil(mContext);
        ethTokenUtil = new EthTokenUtil(mContext);
    }

    public void sendTx(SendTransactionRequest sendTransactionRequest) {
        DialogUtil.showProgressDialog(mContext, mContext.getString(R.string.sending_str));
        if (sendTransactionRequest.hex.equals(SafeGemApplication.getInstance().getLastSendTx())){
            ToastUtil.showShort(mContext, R.string.tx_repeat_send_tips);
            DialogUtil.dismissProgressDialog();
            return;
        }
        if (sendTransactionRequest.coin.equals(Constants.ETH)) {
            SendTransactionRequest.SendETHTransactionRequest sendETHTransactionRequest = new SendTransactionRequest.SendETHTransactionRequest();
            sendETHTransactionRequest.hex = sendTransactionRequest.hex;
            RetrofitManager.getInstance().mNetService.sendEthTransaction(sendETHTransactionRequest)
                    .compose(RxSchedulers.<BaseResponse<EthSendTransationResponse>>compose())
                    .compose(getView().bindLifecycle())
                    .subscribe(new ResponseObserver<EthSendTransationResponse>() {
                        @Override
                        protected void onHandleSuccess(EthSendTransationResponse content) {
                            SafeGemApplication.getInstance().setLastSendTx(sendTransactionRequest.hex);
                            if (getView() != null) {
                                getView().onSendTxComplete(sendTransactionRequest.hex);
                            }
                        }
                    });
        } else if (sendTransactionRequest.coin.equals(Constants.ETC)) {
            SendTransactionRequest.SendETHTransactionRequest sendETHTransactionRequest = new SendTransactionRequest.SendETHTransactionRequest();
            sendETHTransactionRequest.hex = sendTransactionRequest.hex;
            RetrofitManager.getInstance().mNetService.sendEtcTransaction(sendETHTransactionRequest)
                    .compose(RxSchedulers.<BaseResponse<EthSendTransationResponse>>compose())
                    .compose(getView().bindLifecycle())
                    .subscribe(new ResponseObserver<EthSendTransationResponse>() {
                        @Override
                        protected void onHandleSuccess(EthSendTransationResponse content) {
                            SafeGemApplication.getInstance().setLastSendTx(sendTransactionRequest.hex);
                            if (getView() != null) {
                                getView().onSendTxComplete(sendTransactionRequest.hex);
                            }
                        }
                    });
        } else if (sendTransactionRequest.coin.equals(Constants.EOS)) {
            SendTransactionRequest.SendETHTransactionRequest sendETHTransactionRequest = new SendTransactionRequest.SendETHTransactionRequest();
            sendETHTransactionRequest.hex = sendTransactionRequest.hex;
            RetrofitManager.getInstance().mNetService.sendEosTransaction(sendETHTransactionRequest)
                    .compose(RxSchedulers.<BaseResponse<String>>compose())
                    .compose(getView().bindLifecycle())
                    .subscribe(new ResponseObserver<String>() {
                        @Override
                        protected void onHandleSuccess(String content) {
                            SafeGemApplication.getInstance().setLastSendTx(sendTransactionRequest.hex);
                            if (getView() != null) {
                                getView().onSendTxComplete(sendTransactionRequest.hex);
                            }
                        }
                    });
        } else { //发送BTC系列交易
            RetrofitManager.getInstance().mNetService.sendTransaction(sendTransactionRequest)
                    .compose(getView().bindLifecycle())
                    .doOnNext(new Consumer<BaseResponse<BtcSendTransactionResponse>>() {
                        @Override
                        public void accept(BaseResponse<BtcSendTransactionResponse> response) throws Exception {
                            BtcSendTransactionResponse content = response.getData();
                            if (sendTransactionRequest.coin.equals(Constants.USDT)) {
                                txUtil.insertSendTx(content, Constants.BTC);
                            } else {
                                txUtil.insertSendTx(content, sendTransactionRequest.coin);
                            }
                            txUtil.updateTxOuts();
                        }
                    })
                    .compose(RxSchedulers.<BaseResponse<BtcSendTransactionResponse>>compose())
                    .subscribe(new ResponseObserver<BtcSendTransactionResponse>() {
                        @Override
                        protected void onHandleSuccess(BtcSendTransactionResponse content) {
                            SafeGemApplication.getInstance().setLastSendTx(sendTransactionRequest.hex);
                            if (getView() != null) {
                                getView().onSendTxComplete(sendTransactionRequest.hex);
                            }
                        }
                    });
        }


    }

    public void getMainData(Context context) {
        List<MainItemBean> mDatas = new ArrayList();
        mDatas.add(new MainItemBean(R.drawable.erc_img, context.getString(R.string.erc), ERCActivity.class));
        mDatas.add(new MainItemBean(R.drawable.address_img, context.getString(R.string.address), AddressBookActivity.class));
        mDatas.add(new MainItemBean(R.drawable.fee_img, context.getString(R.string.fee_str), BestFeeActivity.class));
        mDatas.add(new MainItemBean(R.drawable.trade_img, context.getString(R.string.trade_str), TransactionCoinActivity.class));
        mDatas.add(new MainItemBean(R.drawable.time_icon, context.getString(R.string.time_adjust_title), TimeAdjustActivity.class));
        mDatas.add(new MainItemBean(R.drawable.code_icon, context.getString(R.string.create_qrcode_str), CreateQrcodeActivity.class));
        mDatas.add(new MainItemBean(R.drawable.eos_icon, context.getString(R.string.eos_manager), null));
        mDatas.add(new MainItemBean(R.drawable.eos_icon, context.getString(R.string.eos_block_message), null));
        if (getView() != null) {
            getView().onMainDataComplete(mDatas);
        }
    }

    /**
     * 添加冷钱包
     */
    public void addColdWallet(AddColdWalletRequest addColdWalletRequest) {
        ColdWalletUtil coldWalletUtil = new ColdWalletUtil(mContext);
        if (coldWalletUtil.queryAllWallet().size() > 5) {
            if (coldWalletUtil.queryWalletCount(addColdWalletRequest.coldUniqueId) < 0) {
                ToastUtil.showShort(mContext, R.string.max_wallet);
                return;
            }
        }
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.addColdWallet(addColdWalletRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<AddColdWalletResponse>>() {
                    @Override
                    public void accept(BaseResponse<AddColdWalletResponse> response) throws Exception {
                        AddColdWalletResponse addColdWalletResponse = response.getData();
                        ColdWalletTb coldWallet = new ColdWalletTb();
                        coldWallet.setColdUniqueId(addColdWalletRequest.coldUniqueId);
                        coldWallet.setColdWalletName(addColdWalletRequest.coldWalletName);
                        coldWallet.setDate(addColdWalletResponse.date);
                        coldWallet.setUserId(SafeGemApplication.getInstance().getUserInfo().getUserId());
                        coldWalletUtil.insertColdWallet(coldWallet);
                    }
                })
                .compose(RxSchedulers.<BaseResponse<BtcSendTransactionResponse>>compose())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (coldWalletUtil.queryWalletCount(addColdWalletRequest.coldUniqueId) > 0) {
                            coldWalletUtil.updateColdWalletName(addColdWalletRequest.coldWalletName, addColdWalletRequest.coldUniqueId);
                            if (getView() != null) {
                                getView().onUpdateColdWalletComplete(addColdWalletRequest.coldUniqueId, addColdWalletRequest.coldWalletName);
                            }
                        }
                    }
                })
                .subscribe(new ResponseObserver<AddColdWalletResponse>() {
                    @Override
                    protected void onHandleSuccess(AddColdWalletResponse addColdWalletResponse) {
                        if (getView() != null) {
                            ToastUtil.showShort(mContext, R.string.add_success);
                            ColdWalletTb coldWallet = new ColdWalletTb();
                            coldWallet.setColdUniqueId(addColdWalletRequest.coldUniqueId);
                            coldWallet.setColdWalletName(addColdWalletRequest.coldWalletName);
                            coldWallet.setDate(addColdWalletResponse.date);
                            coldWallet.setUserId(SafeGemApplication.getInstance().getUserInfo().getUserId());
                            getView().onAddColdWalletComplete(coldWallet);
                        }
                    }
                });
    }

    /**
     * 移除冷钱包
     */
    public void removeColdWallet(String coldUniqueId, int position) {
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.removeColdWallet(new RemoveColdWalletRequest(coldUniqueId))
                .compose(getView().bindLifecycle())
                .compose(RxSchedulers.<BaseResponse<BtcSendTransactionResponse>>compose())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String s) {
                        if (getView() != null) {
                            messageUtils.deleteByColdWallet(coldUniqueId);
                            eosTbUtil.removeEOS(SafeGemApplication.getInstance().getCurrentEOSAccount());
                            ethTokenUtil.deleteERCToken(SafeGemApplication.getInstance().getCurrentETHAddress());
                            monitorAddressUtil.deleteByColdWallet(coldUniqueId);
                            coldWalletUtil.removeColdWallet(coldUniqueId);
                            getView().onRemoveColdWalletComplete(position);
                        }
                    }
                });
    }

    /**
     * EOS获取签名参数
     */
    public void getEosSignParam() {
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.getEosSignParam()
                .compose(getView().bindLifecycle())
                .compose(RxSchedulers.<BaseResponse<GetEosSignResponse>>compose())
                .subscribe(new ResponseObserver<GetEosSignResponse>() {
                    @Override
                    protected void onHandleSuccess(GetEosSignResponse getEosSignResponse) {
                        if (getView() != null) {
                            //传输Eos签名参数
                            TransSignParam signParam = new TransSignParam();
                            signParam.headBlockTime = getEosSignResponse.getHeadBlockTime();
                            signParam.chainId = getEosSignResponse.getChainId();
                            signParam.lastIrreversibleBlockNum = getEosSignResponse.getLastIrreversibleBlockNum();
                            signParam.refBlockPrefix = getEosSignResponse.getRefBlockPrefix();
                            signParam.exp = getEosSignResponse.getExp();
                            String encodeSignParam = ProtoUtils.encodeEosSignParam(signParam);
                            getView().onGetSingComplete(encodeSignParam);
                        }
                    }
                });
    }

    /**
     * 添加监控地址
     */
    public void addColdMonitorAddress(TransMulAddress transMulAddress) {
        long userId = SafeGemApplication.getInstance().getUserIdToLong();
        if (!transMulAddress.walletSeqNumber.equals(SafeGemApplication.getInstance().getColdUniqueId())) {
            ToastUtil.showShort(mContext, mContext.getString(R.string.please_go_wallet_monitor));
            return;
        }

        List<AddColdMonitorAddressRequest.Monitor> monitorList = new ArrayList<>();
        List<MonitorAddressTb> monitorAddressTbList = new ArrayList<>();

        for (Address addressBean : transMulAddress.addressList) {
            if (monitorAddressUtil.isMinerMonitorAddress(userId, addressBean.address, addressBean.coinType)) {
                continue;
            }
            MonitorAddressTb monitorAddressTb = new MonitorAddressTb(userId, transMulAddress.walletSeqNumber, addressBean.coin, addressBean.address, addressBean.coinType, 0);
            monitorAddressTbList.add(monitorAddressTb);
            monitorList.add(new AddColdMonitorAddressRequest.Monitor(addressBean.coin, "", addressBean.address));
            if (addressBean.coinType == Constants.COIN_USDT) { //USDT同步添加BTC到监控地址
                if (monitorAddressUtil.isMinerMonitorAddress(userId, addressBean.address, Constants.COIN_BTC)) {
                    continue;
                }
                MonitorAddressTb btcMonitorAddrTb = new MonitorAddressTb(userId, transMulAddress.walletSeqNumber, Constants.BTC, addressBean.address, Constants.COIN_BTC, 0);
                monitorAddressTbList.add(btcMonitorAddrTb);
                monitorList.add(new AddColdMonitorAddressRequest.Monitor(Constants.BTC, "", addressBean.address));
            } else if(addressBean.coinType == Constants.COIN_EOS){
                checkEosAccount(addressBean.address);
            }
        }

        if (monitorList.size() > 0) {
            DialogUtil.showProgressDialog(mContext);
            RetrofitManager.getInstance().mNetService.addColdMonitorAddress(new AddColdMonitorAddressRequest(transMulAddress.walletSeqNumber, monitorList))
                    .compose(getView().bindLifecycle())
                    .doOnNext(new Consumer<BaseResponse<AddColdMonitorAddressResponse>>() {
                        @Override
                        public void accept(BaseResponse<AddColdMonitorAddressResponse> response) throws Exception {
                            monitorAddressUtil.insertMonitorAddress(monitorAddressTbList);
                        }
                    })
                    .compose(RxSchedulers.<BaseResponse<AddColdMonitorAddressResponse>>compose())
                    .subscribe(new ResponseObserver<AddColdMonitorAddressResponse>() {
                        @Override
                        protected void onHandleSuccess(AddColdMonitorAddressResponse addColdMonitorAddressResponse) {
                            ToastUtil.showShort(mContext, R.string.add_success);
                        }
                    });
        } else {
            ToastUtil.showShort(mContext, R.string.add_success);
        }
    }

    /**
     * 检查EOS账户
     * @param account
     */
    private void checkEosAccount(String account){
        CheckEosAccountRequest checkEosAccountRequest = new CheckEosAccountRequest(account);
        RetrofitManager.getInstance().mNetService.checkEosAccount(checkEosAccountRequest)
                .compose(RxSchedulers.<BaseResponse<CheckEosAccountResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<CheckEosAccountResponse>() {
                    @Override
                    protected void onHandleSuccess(CheckEosAccountResponse datas) {
                        EosAccountUtil eosAccountUtil = new EosAccountUtil(mContext);
                        EosAccountTb eosAccountTb = new EosAccountTb();
                        eosAccountTb.setColdUniqueId(SafeGemApplication.getInstance().getColdUniqueId());
                        eosAccountTb.setAccount(account);
                        eosAccountTb.setActivePubKey(datas.getActivePubKey());
                        eosAccountTb.setOwnerPubKey(datas.getOwnerPubKey());
                        eosAccountUtil.insertEosAccountTb(eosAccountTb);
                    }
                });
    }

    /**
     * 获取消息列表
     */
    public void getMsgList() {
        RetrofitManager.getInstance().mNetService.getMsgList()
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<List<GetMsgListResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<GetMsgListResponse>> response) throws Exception {
                        List<GetMsgListResponse> getMsgListResponseList = response.getData();
                        List<MessageTb> messageTbList = new ArrayList<>();
                        MessageUtils messageUtils = new MessageUtils(mContext);
                        for (GetMsgListResponse getMsgListResponse : getMsgListResponseList) {
                            if (!messageUtils.isExitMessage(getMsgListResponse.msgId)) {
                                MessageTb messageTb = new MessageTb(getMsgListResponse.msgId, SafeGemApplication.getInstance().getUserInfo().getUserId(), getMsgListResponse.title,
                                        getMsgListResponse.content, 1, getMsgListResponse.source, getMsgListResponse.date, getMsgListResponse.msgUrl, "", -1, getMsgListResponse.icon, SafeGemApplication.getInstance().getColdUniqueId());
                                messageTb.setColdUniqueId("");
                                messageTbList.add(messageTb);
                            }
                        }
                        if (messageTbList.size() > 0) {
                            messageUtils.insertColdWallets(messageTbList);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<AddColdMonitorAddressResponse>>compose())
                .subscribe(new ResponseObserver<List<GetMsgListResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<GetMsgListResponse> getMsgListResponseList) {
                        if (getView() != null) {
                            getView().onGetMessageComplete();
                        }
                    }
                });
    }

    /**
     * 冷热端版本更新
     */
    public void getAppVersion(String version, int productType) {
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.getAppVersion(new GetAppVersionRequest(version, String.valueOf(productType)))
                .compose(RxSchedulers.<BaseResponse<GetAppVersionResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<GetAppVersionResponse>() {
                    @Override
                    protected void onHandleSuccess(GetAppVersionResponse getAppVersionResponse) {
                        if (getView() != null) {
                            if (getAppVersionResponse.lastVer == Constants.HAS_NEW_VERSION) {
                                getView().onHasNewVersion(getAppVersionResponse, productType);
                            } else {
                                getView().onNoNewVersion(productType);
                            }
                        }
                    }
                });
    }

    /**
     * 下载
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void downloadApk(String url) {
        RingManager.getInstance().playBeep();
        DownloadClient.initClient_BaseUrl(url + "/");
        Disposable d = DownloadClient.getInstance()
                .downloadFile(url,
                        Constants.Companion.getHOT_WALLET_PATH(), Constants.Companion.getHOT_WALLET_FILE_NAME() + ".apk",
                        new FileDownLoadObserver<File>() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                if (getView() != null) {
                                    getView().onDownloadSuccess(file);
                                }
                            }

                            @Override
                            public void onDownloadFail(Throwable throwable) {
                                if (getView() != null) {
                                    getView().onDownloadFail(throwable);
                                }
                            }

                            @Override
                            public void onProgress(int progress, long total) {
                                if (getView() != null) {
                                    getView().onProgress(progress, total);
                                }
                            }
                        });
        DownloadApiManager.getInstance().add(d);
    }

}
