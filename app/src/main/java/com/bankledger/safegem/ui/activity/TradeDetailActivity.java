package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.TransactionDetailBean;
import com.bankledger.safegem.greendaodb.EosRamUtil;
import com.bankledger.safegem.greendaodb.SafeAssetUtil;
import com.bankledger.safegem.greendaodb.entity.EosRamTb;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.presenter.TransactionDetailPresenter;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.ConfigUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;
import com.bankledger.safegem.view.ITransactionDetailView;
import com.google.zxing.WriterException;


import butterknife.BindView;

public class TradeDetailActivity extends MVPBaseActivity<ITransactionDetailView, TransactionDetailPresenter> implements ITransactionDetailView {

    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @BindView(R.id.tv_miner_fee)
    TextView tvMinerFee;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_block_height)
    TextView tvBlockHeight;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.code_img)
    ImageView codeImg;

    @BindView(R.id.asset_tips)
    TextView assetTips;
    @BindView(R.id.asset_amount)
    TextView assetAmount;

    @BindView(R.id.copy)
    TextView copy;

    private String txHash;
    private String coldUniqueId;
    private int coinType;
    private SafeAssetUtil safeAssetUtil;
    private SafeAssetTb safeAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_detail);
        setTitle(getString(R.string.trade_detail));

    }

    @Override
    protected TransactionDetailPresenter createPresenter() {
        return new TransactionDetailPresenter(this);
    }

    @Override
    public void initData() {
        super.initData();
        safeAssetUtil = new SafeAssetUtil(this);
        txHash = getIntent().getStringExtra(Constants.INTENT_KEY1);
        coldUniqueId = getIntent().getStringExtra(Constants.INTENT_KEY2);
        if (TextUtils.isEmpty(coldUniqueId)) {
            coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        }
        coinType = getIntent().getIntExtra(Constants.INTENT_KEY3, Constants.COIN_BTC);
        if (coinType == Constants.COIN_BTC || coinType == Constants.COIN_SAFE_ASSET) { //BTC
            if (coinType == Constants.COIN_SAFE_ASSET) {
                safeAsset = safeAssetUtil.querySafeAssetWithTxHash(txHash);
            }
            mPresenter.getTransactionDetail(txHash, coldUniqueId);
        } else if (coinType == Constants.COIN_USDT) { //USDT
            mPresenter.getUsdtTransactionDetail(txHash);
        } else if (coinType == Constants.COIN_EOS || coinType == Constants.COIN_EOS_TOKEN) { //EOS
            mPresenter.getEosTransactionDetail(txHash);
        }

    }

    @Override
    public void onGetTransactionDetail(TransactionDetailBean tx) {
        String amount = BigDecimalUtils.formatBtc(tx.getAmount());
        if (BigDecimalUtils.greaterThan(amount, "0")) {
            amount = " + " + amount;
        }
        tvAmount.setText(StringUtil.subZeroAndDot(amount));
        if (safeAsset != null) {
            tvUnit.setText(safeAsset.getAssetUnit());
        } else {
            tvUnit.setText(StringUtil.getDisplayName(tx.getCoin()));
        }
        tvReceiveAddress.setText(tx.getReceiveAddress());
        if (tx.getMinerFee().equals("0")) {
            tvMinerFee.setText(getString(R.string.not_have));
        } else {
            if (safeAsset != null) {
                tvMinerFee.setText(BigDecimalUtils.formatBtc(tx.getMinerFee()) + " " + Constants.SAFE);
            } else {
                tvMinerFee.setText(BigDecimalUtils.formatBtc(tx.getMinerFee()));
            }
        }
        tvId.setText(tx.getTxHash());
        tvBlockHeight.setText(Long.toString(tx.getHeight()));
        tvDate.setText(DateTimeUtil.getDateTimeString(tx.getTime() * 1000));
        try {
            codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, tvId.getText().toString()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        setBrowserUrl(tx.getCoin());
    }

    @Override
    public void onGetEosTransactionDetail(EosTxTb tx) {
        EosRamUtil eosRamUtil = new EosRamUtil(this);
        if (tx.getType() == 1) {
            EosRamTb ramTb = eosRamUtil.queryEosRamTbForTxId(tx.getTxId());
            tvReceiveAddress.setText(ramTb.getTo());
        } else {
            tvReceiveAddress.setText(tx.getTo());
        }
        if (tx.getFrom().equals(tx.getAccount())) {
            if (tx.getType() == 1) {
                EosRamTb ramTb = eosRamUtil.queryEosRamTbForTxId(tx.getTxId());
                String amount = ramTb.getQuantity().split(" ")[0];
                tvAmount.setText(" - " + BigDecimalUtils.add(amount, tx.getAmount()));
            } else {
                tvAmount.setText(" - " + tx.getAmount());
            }
            tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_color));
        } else {
            if (tx.getType() == 1) {
                EosRamTb ramTb = eosRamUtil.queryEosRamTbForTxId(tx.getTxId());
                String amount = ramTb.getQuantity().split(" ")[0];
                tvAmount.setText(" + " + BigDecimalUtils.add(amount, tx.getAmount()));

            } else {
                tvAmount.setText(" + " + tx.getAmount());
            }
            tvAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
        }
        tvUnit.setText(tx.getCoin());
        tvMinerFee.setText(getString(R.string.not_have));
        tvId.setText(tx.getTxId());
        tvBlockHeight.setText(tx.getHeight());
        tvDate.setText(tx.getFormatDateTime());
        setBrowserUrl(Constants.EOS);
    }

    @Override
    public void onGetUsdtTransactionDetail(UsdtTxTb tx) {
        String address = SafeGemApplication.getInstance().getCurrentUSDTAddress();
        tvReceiveAddress.setText(tx.getReferenceaddress());
        if (tx.getSendingaddress().equals(address)) {
            tvAmount.setText(" - " + tx.getAmount());
            tvMinerFee.setText(tx.getFee() + " " + Constants.BTC);
            assetTips.setText(R.string.usdt_send_btc);
            assetAmount.setText("-0.00000546 " + Constants.BTC);
        } else {
            tvAmount.setText(" + " + tx.getAmount());
            tvMinerFee.setText(getString(R.string.not_have));
            assetTips.setText(R.string.usdt_receive_btc);
            assetAmount.setText("+0.00000546 " + Constants.BTC);
        }
        tvUnit.setText(Constants.USDT);
        tvId.setText(tx.getTxid());
        tvBlockHeight.setText(tx.getBlock());
        tvDate.setText(tx.getFormatDateTime());
        try {
            codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, tvId.getText().toString()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mPresenter.getUsdtBtcTransactionDetail(txHash, coldUniqueId);
        setBrowserUrl(Constants.USDT);
    }

    @Override
    public void onGetUsdtBtcTransactionDetail(TransactionDetailBean tx) {
        if (tx != null && !TextUtils.isEmpty(tx.getTxHash())) {
            if (tx.isSend()) {
                assetAmount.setText(BigDecimalUtils.formatBtc(tx.getAmount()) + " " + Constants.BTC);
            } else {
                assetAmount.setText(BigDecimalUtils.formatBtc(tx.getAmount()) + " " + Constants.BTC);
            }
        }
    }

    public void setBrowserUrl(String coin) {
        String url = ConfigUtil.getInstance().getBrowserUrl(coin);
        if (!TextUtils.isEmpty(url)) {
            copy.setText(R.string.browser);
            try {
                codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, url + txHash));
            } catch (WriterException e) {
                e.printStackTrace();
            }
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url + txHash);
                    intent.setData(content_url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    startActivity(intent);

                }
            });
        } else {
            copy.setText(getString(R.string.copy_url));
            try {
                codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, txHash));
            } catch (WriterException e) {
                e.printStackTrace();
            }
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyContent(tvId.getText().toString(), TradeDetailActivity.this);
                }
            });
        }
    }

}
