package com.bankledger.safegem.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.ui.view.CommonPopupWindow;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.google.zxing.WriterException;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateQrcodeActivity extends BaseActivity {

    @BindView(R.id.edit)
    EditText edit;
    private CommonPopupWindow popupWindow;
    private View upView;
    private Bitmap bitmap;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qrcode);
        setTitle(R.string.create_qrcode_str);
        upView = LayoutInflater.from(this).inflate(R.layout.create_code_popup_layout, null, false);
        upView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        upView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        img=upView.findViewById(R.id.viewPager);
    }

    @OnClick(R.id.create_btn)
    public void onViewClicked() {
        if (edit.getText().toString().length() == 0){
            ToastUtil.showShort(this, R.string.inpu_text_content);
        }else {
            try {
                bitmap= QRCodeEncoderUtils.encodeAsBitmap(this, edit.getText().toString());
                img.setImageBitmap(bitmap);
                popupWindow = new CommonPopupWindow.Builder(this)
                        .setView(upView)
                        .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setBackGroundLevel(0.5f)
                        .setOutsideTouchable(true)
                        .create();
                show();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    public void show() {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null){
            bitmap.recycle();
        }
    }
}
