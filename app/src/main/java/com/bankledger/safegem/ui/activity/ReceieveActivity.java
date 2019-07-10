package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.entity.CoinEntity;
import com.bankledger.safegem.entity.MonitorAddressEntity;
import com.bankledger.safegem.greendaodb.MonitorAddressUtil;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.utils.BitmapUtil;
import com.bankledger.safegem.utils.ConfigUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.EditTextJudgeNumber;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;
import com.google.zxing.WriterException;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ReceieveActivity extends BaseActivity {

    @BindView(R.id.coin_img)
    ImageView coinImg;
    @BindView(R.id.coin_name)
    TextView coinName;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.code_img)
    ImageView codeImg;
    @BindView(R.id.amout_edit)
    EditText amoutEdit;
    @BindView(R.id.arrow_img)
    ImageView arrowImg;
    @BindView(R.id.arrow_img_address)
    ImageView arrow_img_address;

    private Bitmap bm;
    private String coldUniqueId;
    private int coinType;
    private String coin;
    private String assetName;
    private MonitorAddressTb monitorAddressTb;
    private MonitorAddressUtil monitorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receieve);
        setTitle(getString(R.string.receive_str));
        coldUniqueId = getIntent().getStringExtra(Constants.WALLET_ID_DATA);
        monitorUtil = new MonitorAddressUtil(this);
        if (coldUniqueId != null) {
            monitorAddressTb = monitorUtil.queryFirstMonitorAddress(SafeGemApplication.getInstance().getUserId(), coldUniqueId);
            coin = monitorAddressTb.getCoin();
            coinType = monitorAddressTb.getCoinType();
            setView();
        } else {
            monitorAddressTb = GsonUtils.getObjFromJSON(getIntent().getStringExtra(Constants.INTENT_DATA), MonitorAddressTb.class);
            MonitorAddressEntity monitorAddressEntity = GsonUtils.getObjFromJSON(getIntent().getStringExtra(Constants.INTENT_KEY1), MonitorAddressEntity.class);
            coin = monitorAddressTb.getCoin();
            coinType = monitorAddressEntity.coinType;
            if(coinType == Constants.COIN_SAFE_ASSET){
                assetName = monitorAddressEntity.name;
            }
            arrow_img_address.setVisibility(View.GONE);
            arrowImg.setVisibility(View.GONE);
            setView();
        }

        amoutEdit.addTextChangedListener(new EditTextJudgeNumber(amoutEdit, 10, 8, true));

        amoutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    bm = ((BitmapDrawable) (codeImg).getDrawable()).getBitmap();
                    if (bm != null) {
                        bm.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    updateCodeImg();
                }
            }
        });

    }

    public String getName() {
        if (coinType == Constants.COIN_SAFE_ASSET) {
            return assetName;
        } else if (coinType == Constants.COIN_EOS_TOKEN) {
            return coin;
        } else if (coinType == Constants.COIN_ETH_TOKEN) {
            return monitorAddressTb.getContractAddress();
        } else {
            String fullName = ConfigUtil.getInstance().getCoinFullName(monitorAddressTb.getCoin());
            if (!TextUtils.isEmpty(fullName)) {
                return fullName;
            } else {
                return monitorAddressTb.getCoin();
            }
        }
    }

    public void setView() {
        if (monitorAddressTb != null) {
            if (coinType == Constants.COIN_ETH_TOKEN) {
                BitmapUtil.loadImage(monitorAddressTb.getCoinImg(), coinImg, R.drawable.eth_img);
                coinName.setText(monitorAddressTb.getSymbol());
                addressTv.setText(monitorAddressTb.getAddress());
            } else if(coinType == Constants.COIN_SAFE_ASSET){
                coinImg.setImageResource(Utils.getCoinImg(coin, coinType));
                coinName.setText(StringUtil.getDisplayName(assetName));
                addressTv.setText(monitorAddressTb.getAddress());
            } else {
                coinImg.setImageResource(Utils.getCoinImg(coin, coinType));
                coinName.setText(StringUtil.getDisplayName(coin));
                addressTv.setText(monitorAddressTb.getAddress());
            }
            if (coinType == Constants.COIN_BTC || coinType == Constants.COIN_SAFE_ASSET) {
                arrow_img_address.setVisibility(View.VISIBLE);
            } else {
                arrow_img_address.setVisibility(View.GONE);
            }
            updateCodeImg();
        }
    }

    private void updateCodeImg() {
        try {
            codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, Utils.getCodeContent(getName(), addressTv.getText().toString(), amoutEdit.getText().toString(), coinType)));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.coin_view, R.id.address, R.id.copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.coin_view:
                if (coldUniqueId != null && monitorAddressTb != null) {
                    Intent intent1 = new Intent(ReceieveActivity.this, SelectCoinActivity.class);
                    intent1.putExtra(Constants.INTENT_DATA, coldUniqueId);
                    startActivityForResult(intent1, Constants.REQUEST_CODE);
                }
                break;
            case R.id.address:
                if (coldUniqueId != null && monitorAddressTb != null && (coinType == Constants.COIN_BTC || coinType == Constants.COIN_SAFE_ASSET)) {
                    Intent intent2 = new Intent(ReceieveActivity.this, SelectAddressActivity.class);
                    intent2.putExtra(Constants.INTENT_KEY1, coinType == Constants.COIN_SAFE_ASSET ? Constants.COIN_BTC : coinType);
                    intent2.putExtra(Constants.INTENT_KEY2, coin);
                    startActivityForResult(intent2, Constants.REQUEST_CODE);
                }
                break;
            case R.id.copy:
                copy();
                break;
        }
    }

    public void copy() {
        String text = addressTv.getText().toString();
        Utils.copyContent(text, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bm != null) {
            bm.recycle();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && resultCode == Constants.MONITOR_ADDRESS) {
            monitorAddressTb = GsonUtils.getObjFromJSON(data.getStringExtra(Constants.INTENT_DATA), MonitorAddressTb.class);
            setView();
        } else if (requestCode == Constants.REQUEST_CODE && resultCode == Constants.RESULT_CODE) {
            CoinEntity coinEntity = GsonUtils.getObjFromJSON(data.getStringExtra(Constants.INTENT_DATA), CoinEntity.class);
            if (coinEntity != null) {
                coin = coinEntity.coin;
                coinType = coinEntity.coinType;
                String tempCoin;
                int tempCoinType;
                if(coinType == Constants.COIN_SAFE_ASSET){
                    assetName = coinEntity.assetName;
                    tempCoin = coin;
                    tempCoinType = Constants.COIN_BTC;
                } else if (coinType == Constants.COIN_EOS_TOKEN){
                    tempCoin = Constants.EOS;
                    tempCoinType = Constants.COIN_EOS;
                } else {
                    tempCoin = coin;
                    tempCoinType = coinType;
                }
                List<MonitorAddressTb> addressTbList = monitorUtil.queryUserCoinMonitorAddressTbs(tempCoin, tempCoinType);
                if (addressTbList != null && addressTbList.size() > 0) {
                    monitorAddressTb = addressTbList.get(0);
                    setView();
                }
            }
        }
    }

}
