package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.ColdWalletUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.UserTb;
import com.bankledger.safegem.greendaodb.UserUtil;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.GetColdMonitorAddressRequest;
import com.bankledger.safegem.net.model.request.GetERCListRequest;
import com.bankledger.safegem.net.model.request.LoginRequest;
import com.bankledger.safegem.net.model.request.RegisteRequest;
import com.bankledger.safegem.net.model.response.AddColdWalletResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.net.model.response.GetColdMonitorAddressResponse;
import com.bankledger.safegem.net.model.response.ModifyPasswordResponse;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.MoreRequestDialogUtil;
import com.bankledger.safegem.view.ISetPwdView;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class SetPwdPresenter extends BasePresenter<ISetPwdView> {

    private Context mContext;
    private UserUtil userTbUtils;
    private ColdWalletUtil coldWalletUtil;

    public SetPwdPresenter(Context mContext) {
        this.mContext = mContext;
        this.userTbUtils = new UserUtil(mContext);
        this.coldWalletUtil = new ColdWalletUtil(mContext);
    }

    public void register(String phone, String sms, String pwd) {
        RegisteRequest registeRequest = new RegisteRequest(phone, sms, pwd);
        DialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.register(registeRequest)
                .compose(RxSchedulers.<BaseResponse<UserTb>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<UserTb>() {
                    @Override
                    protected void onHandleSuccess(UserTb userInform) {
                        if (getView() != null && userInform != null) {
                            userInform.setIsLogin(true);
                            if (userTbUtils.insertUser(userInform)) {
                                getView().onRegisterSuccess();
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                    }
                });
    }

    /**
     * 登录
     */
    public void login(String phone, String pwd) {
        LoginRequest loginRequest = new LoginRequest(phone, pwd);
        MoreRequestDialogUtil.showProgressDialog(mContext);
        RetrofitManager.getInstance().mNetService.login(loginRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<UserTb>>() {
                    @Override
                    public void accept(BaseResponse<UserTb> response) throws Exception {
                        UserTb userTb = response.getData();
                        if (userTb != null) {
                            userTb.setIsLogin(true);
                            userTbUtils.insertUser(userTb);
                            SafeGemApplication.getInstance().setUser(userTb);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<UserTb>>compose())
                .subscribe(new ResponseObserver<UserTb>() {
                    @Override
                    protected void onHandleSuccess(UserTb userInfo) {
                        if (getView() != null && userInfo != null) {
                            getColdWalletList();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        MoreRequestDialogUtil.dismissProgressDialog();
                    }

                });
    }

    public void modifyPassword(String phone, String sms, String pwd) {
        RegisteRequest registeRequest = new RegisteRequest(phone, sms, pwd);
        RetrofitManager.getInstance().mNetService.modifyPassword(registeRequest)
                .compose(RxSchedulers.<BaseResponse<ModifyPasswordResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<ModifyPasswordResponse>() {
                    @Override
                    protected void onHandleSuccess(ModifyPasswordResponse userInform) {
                        if (getView() != null && userInform != null) {
                            getView().onModifyPasswordSuccess();
                        }
                    }
                });
    }

    /**
     * 获取冷钱包并保存
     */
    public void getColdWalletList() {
        RetrofitManager.getInstance().mNetService.getColdWalletList()
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<List<AddColdWalletResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<AddColdWalletResponse>> response) throws Exception {
                        List<AddColdWalletResponse> addColdWalletResponseList = response.getData();
                        List<ColdWalletTb> addColdWalletResponses = new ArrayList<>();
                        for (AddColdWalletResponse addColdWalletResponse : addColdWalletResponseList) {
                            ColdWalletTb coldWallet = new ColdWalletTb();
                            coldWallet.setColdUniqueId(addColdWalletResponse.coldUniqueId);
                            coldWallet.setColdWalletName(addColdWalletResponse.coldWalletName);
                            coldWallet.setDate(addColdWalletResponse.date);
                            coldWallet.setUserId(SafeGemApplication.getInstance().getUserInfo().getUserId());
                            addColdWalletResponses.add(coldWallet);
                        }
                        if (coldWalletUtil.insertColdWallets(addColdWalletResponses)) {
                            if (addColdWalletResponses.size() > 0) {
                                SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.CURRENT_WALLET_ID, addColdWalletResponses.get(0).getColdUniqueId());
                                SafeGemApplication.getInstance().appSharedPrefUtil.putInt(Constants.CURRENT_WALLET_POSITION, 0);
                            }
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<UserTb>>compose())
                .subscribe(new ResponseObserver<List<AddColdWalletResponse>>() {

                    @Override
                    protected void onHandleSuccess(List<AddColdWalletResponse> addColdWalletResponseList) {
                        getColdMonitorAddress(addColdWalletResponseList);
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        MoreRequestDialogUtil.dismissProgressDialog();
                    }

                });
    }

    /**
     * 获取监控地址并保存
     */
    public void getColdMonitorAddress(List<AddColdWalletResponse> addColdWalletResponseList) {
        GetColdMonitorAddressRequest getColdMonitorAddressRequest = new GetColdMonitorAddressRequest();
        for (AddColdWalletResponse addColdWalletResponse : addColdWalletResponseList) {
            getColdMonitorAddressRequest.coldUniqueIdList.add(addColdWalletResponse.coldUniqueId);
        }
        RetrofitManager.getInstance().mNetService.getColdMonitorAddress(getColdMonitorAddressRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<List<GetColdMonitorAddressResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<GetColdMonitorAddressResponse>> response) throws Exception {
                        List<GetColdMonitorAddressResponse> getColdMonitorAddressResponseList = response.getData();
                        List<MonitorAddressTb> monitorAddressTbList = new ArrayList<>();
                        for (GetColdMonitorAddressResponse montorAddrssResponse : getColdMonitorAddressResponseList) {
                            MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(), montorAddrssResponse.coldUniqueId, montorAddrssResponse.coin, montorAddrssResponse.address, 0);
                            monitorAddressTbList.add(monitorAddressTb);
                        }
                        new MonitorAddressUtil(mContext).insertMonitorAddress(monitorAddressTbList);
                    }
                })
                .compose(RxSchedulers.<BaseResponse<UserTb>>compose())
                .subscribe(new ResponseObserver<List<GetColdMonitorAddressResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<GetColdMonitorAddressResponse> getColdMonitorAddressResponseList) {
                        getERC20Token();
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        MoreRequestDialogUtil.dismissProgressDialog();
                    }

                });
    }

    /**
     * 获取erc
     */
    public void getERC20Token() {
        DialogUtil.showProgressDialog(mContext);
        List<String> list = new ColdWalletUtil(mContext).queryUserWalletId();
        GetERCListRequest getERCListRequest = new GetERCListRequest();
        MonitorAddressUtil monitorAddressUtil = new MonitorAddressUtil(mContext);
        getERCListRequest.coldUniqueIdList.addAll(list);
        RetrofitManager.getInstance().mNetService.getERC20List(getERCListRequest)
                .compose(getView().bindLifecycle())
                .doOnNext(new Consumer<BaseResponse<List<ERCResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<ERCResponse>> response) throws Exception {
                        List<ERCResponse> content = response.getData();
                        List<MonitorAddressTb> monitorAddressTbList = new ArrayList<>();
                        for (ERCResponse ercResponse : content) {
                            for (ERCResponse.ERC erc : ercResponse.erc20List) {
                                if (erc.getActivication() == 1) {
                                    MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(),
                                            ercResponse.coldUniqueId, erc.getName(), monitorAddressUtil.queryUserETHMonitorAddress(ercResponse.coldUniqueId), 0,
                                            erc.getContractAddress(), erc.getSymbol(), erc.getDecimals(), erc.getTotalSupply(), Constants.COIN_ETH_TOKEN, erc.getIcon());
                                    monitorAddressTbList.add(monitorAddressTb);
                                }
                            }
                        }
                        if (monitorAddressTbList.size() > 0) {
                            monitorAddressUtil.insertMonitorAddress(monitorAddressTbList);
                        }
                    }
                })
                .compose(RxSchedulers.<BaseResponse<UserTb>>compose())
                .subscribe(new ResponseObserver<List<ERCResponse>>() {
                    @Override
                    protected void onHandleSuccess(List<ERCResponse> content) {
                        if (getView() != null) {
                            MoreRequestDialogUtil.dismissProgressDialog();
                            getView().onRegisterSuccess();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        MoreRequestDialogUtil.dismissProgressDialog();
                    }

                });
    }

}
