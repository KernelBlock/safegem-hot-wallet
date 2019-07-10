package com.bankledger.safegem.ui.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bankledger.protobuf.bean.TransRetEos;
import com.bankledger.protobuf.utils.ProtoUtils;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.EosAccountUtil;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.net.model.response.EosAccountResponse;
import com.bankledger.safegem.presenter.EosAccountPresenter;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.view.EosAccountView;
import com.google.zxing.WriterException;

import butterknife.BindView;

public class EosAccountActivity extends MVPBaseActivity<EosAccountView, EosAccountPresenter> implements EosAccountView {

    @BindView(R.id.code_img)
    ImageView codeImg;

    @BindView(R.id.tv_account)
    TextView tvAccount;

    @BindView(R.id.tv_mine_ram)
    TextView tvMineRam;
    @BindView(R.id.pb_ram)
    ProgressBar pbRam;

    @BindView(R.id.tv_mine_cpu)
    TextView tvMineCpu;
    @BindView(R.id.pb_cpu)
    ProgressBar pbCpu;

    @BindView(R.id.tv_mine_net)
    TextView tvMineNet;
    @BindView(R.id.pb_net)
    ProgressBar pbNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eos_account);
        setTitle(R.string.eos_manager);
        String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
        mPresenter.getEosAccount(account);
        tvAccount.setText(account);
        setQrCode(account);
    }

    @Override
    protected EosAccountPresenter createPresenter() {
        return new EosAccountPresenter(this);
    }


    private void setQrCode(String account) {
        TransRetEos retEos = new TransRetEos();
        retEos.walletSeqNumber = SafeGemApplication.getInstance().getColdUniqueId();
        EosAccountUtil eosAccountUtil = new EosAccountUtil(this);
        EosAccountTb eosAccountTb = eosAccountUtil.queryCurrentEosAccountTb(retEos.walletSeqNumber, account);
        if(eosAccountTb != null){
            retEos.account = account;
            retEos.active = eosAccountTb.getActivePubKey();
            retEos.owner = eosAccountTb.getOwnerPubKey();
            String encodeRetEos = ProtoUtils.encodeRetEos(retEos);
            Bitmap bitmap;
            try {
                bitmap = QRCodeEncoderUtils.encodeAsBitmap(this, encodeRetEos);
                codeImg.setVisibility(View.VISIBLE);
                codeImg.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            retEos.account = account;
            retEos.active = "";
            retEos.owner = "";
            String encodeRetEos = ProtoUtils.encodeRetEos(retEos);
            Bitmap bitmap;
            try {
                bitmap = QRCodeEncoderUtils.encodeAsBitmap(this, encodeRetEos);
                codeImg.setVisibility(View.VISIBLE);
                codeImg.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGetEosAccount(EosAccountResponse account) {
        tvAccount.setText(account.account_name);
        setQrCode(account.account_name);

        tvMineRam.setText(StringUtil.formatKb(account.ram_usage) + " KB / " + StringUtil.formatKb(account.ram_quota) + " KB");
        int ramPro = (int) ((account.ram_usage * 1.0f / account.ram_quota) * 100);
        pbRam.setProgress(ramPro);

        tvMineCpu.setText(StringUtil.formatMs(account.cpu_limit.used) + " ms / " + StringUtil.formatMs(account.cpu_limit.max) + " ms");
        int cpuPro = (int) ((account.cpu_limit.used * 1.0f / account.cpu_limit.max) * 100);
        pbCpu.setProgress(cpuPro);

        tvMineNet.setText(StringUtil.formatKb(account.net_limit.used) + " KB / " + StringUtil.formatKb(account.net_limit.max) + " KB");
        int netPro = (int) ((account.net_limit.used * 1.0f / account.net_limit.max) * 100);

        pbNet.setProgress(netPro);
    }

    @Override
    public void onError(String account) {
        tvAccount.setText(account);
        setQrCode(account);
    }

}
