package com.bankledger.safegem.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.LogUtils;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.google.zxing.WriterException;

/**
 * Created by zm on 2018/7/4.
 */

public class QrCodeFragment extends BaseFragment{

    private ImageView mImgQrcode;
    private Bitmap bm;
    @Override
    public int setContentView() {
        return R.layout.fragment_qr_code;
    }

    @Override
    public void initView() {
        mImgQrcode = findViewById(R.id.img_qrcode);
    }

    @Override
    public void initData() {
        Bundle args = getArguments();
        String hexTx = args.getString(Constants.INTENT_KEY1);
        try {
            mImgQrcode.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(getContext(), hexTx));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bm =((BitmapDrawable) ((ImageView) mImgQrcode).getDrawable()).getBitmap();
        if (bm != null){
            bm.recycle();
        }
    }
}
