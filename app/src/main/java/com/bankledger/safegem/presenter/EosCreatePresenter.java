package com.bankledger.safegem.presenter;

import android.content.Context;

import com.bankledger.protobuf.bean.TransEos;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.EosAccountUtil;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.net.ResponseObserver;
import com.bankledger.safegem.net.RetrofitManager;
import com.bankledger.safegem.net.RxSchedulers;
import com.bankledger.safegem.net.model.request.AddColdMonitorAddressRequest;
import com.bankledger.safegem.net.model.request.CheckEosAccountRequest;
import com.bankledger.safegem.net.model.request.RegisterEosAccountRequest;
import com.bankledger.safegem.net.model.response.AddColdMonitorAddressResponse;
import com.bankledger.safegem.net.model.response.BaseResponse;
import com.bankledger.safegem.net.model.response.CheckEosAccountResponse;
import com.bankledger.safegem.net.model.response.RegisterEosAccountResponse;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.view.EosCreateView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2018/12/11.
 */

public class EosCreatePresenter extends BasePresenter<EosCreateView> {

    private Context mContext;

    public EosCreatePresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 检查EOS账户
     * @param account
     */
    public void checkEosAccount(String account) {
        DialogUtil.showProgressDialog(mContext);
        CheckEosAccountRequest checkEosAccountRequest = new CheckEosAccountRequest(account);
        RetrofitManager.getInstance().mNetService.checkEosAccount(checkEosAccountRequest)
                .compose(RxSchedulers.<BaseResponse<CheckEosAccountResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<CheckEosAccountResponse>() {
                    @Override
                    protected void onHandleSuccess(CheckEosAccountResponse datas) {
                        if (getView() != null && datas != null) {
                            getView().onCheckEosAccountComplete(datas);
                        }
                    }
                });
    }

    /**
     * 新建EOS账户
     * @param transEos
     */
    public void registerEosAccount(TransEos transEos) {
        DialogUtil.showProgressDialog(mContext);
        RegisterEosAccountRequest registerEosAccountRequest = new RegisterEosAccountRequest(transEos.account, transEos.active, transEos.owner, transEos.walletSeqNumber, transEos.deviceName);
        RetrofitManager.getInstance().mNetService.registerEosAccount(registerEosAccountRequest)
                .compose(RxSchedulers.<BaseResponse<RegisterEosAccountResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<RegisterEosAccountResponse>() {
                    @Override
                    protected void onHandleSuccess(RegisterEosAccountResponse datas) {
                        addMonotior(transEos, datas.getNewAccount(), transEos.opType);
                    }
                });
    }

    /**
     * 添加监控账户
     * @param account
     * @param opType
     */
    public void addMonotior(TransEos transEos, String account, int opType) {
        DialogUtil.showProgressDialog(mContext);
        addEosAccount(transEos, account);
        List<AddColdMonitorAddressRequest.Monitor> monitorList = new ArrayList<AddColdMonitorAddressRequest.Monitor>();
        monitorList.add(new AddColdMonitorAddressRequest.Monitor(Constants.EOS, "", account));
        RetrofitManager.getInstance().mNetService.addColdMonitorAddress(new AddColdMonitorAddressRequest(SafeGemApplication.getInstance().getColdUniqueId(), monitorList))
                .compose(RxSchedulers.<BaseResponse<AddColdMonitorAddressResponse>>compose())
                .compose(getView().bindLifecycle())
                .subscribe(new ResponseObserver<AddColdMonitorAddressResponse>() {
                    @Override
                    protected void onHandleSuccess(AddColdMonitorAddressResponse datas) {
                        MonitorAddressUtil monitorAddressUtil = new MonitorAddressUtil(mContext);
                        MonitorAddressTb monitorAddressTb = new MonitorAddressTb(SafeGemApplication.getInstance().getUserIdToLong(), SafeGemApplication.getInstance().getColdUniqueId(), Constants.EOS, account, Constants.COIN_EOS, 0);
                        monitorAddressUtil.insertMonitorAddress(monitorAddressTb);
                        if (getView() != null && datas != null) {
                            getView().onRegisterEosAccountComplete(account, opType);
                        }
                    }
                });
    }

    private void addEosAccount(TransEos transEos, String account){
        EosAccountUtil eosAccountUtil = new EosAccountUtil(mContext);
        EosAccountTb eosAccountTb = new EosAccountTb();
        eosAccountTb.setColdUniqueId(SafeGemApplication.getInstance().getColdUniqueId());
        eosAccountTb.setAccount(account);
        eosAccountTb.setActivePubKey(transEos.active);
        eosAccountTb.setOwnerPubKey(transEos.owner);
        eosAccountUtil.insertEosAccountTb(eosAccountTb);
    }

}
