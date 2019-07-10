package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.protobuf.bean.CommonMsg;
import com.bankledger.protobuf.bean.TransDate;
import com.bankledger.protobuf.utils.ProtoUtils;
import com.bankledger.safegem.R;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class TimeAdjustActivity extends BaseActivity {

    @BindView(R.id.code_img)
    ImageView codeImg;
    @BindView(R.id.time_tv)
    TextView timeTv;
    private final int count = 10;
    private Disposable db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_adjust);
        ButterKnife.bind(this);
        setTitle(getString(R.string.time_adjust_title));
        timeTv.setText(getString(R.string.time_refresh, "" + count));
        getTime();

    }

    private void setTime() {
        try {
            if (codeImg != null) {
                TransDate dateBean = new TransDate(DateTimeUtil.getDateTimeString(System.currentTimeMillis()));
                String encodeDate = ProtoUtils.encodeDate(dateBean);
                codeImg.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(this, encodeDate));
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void getTime() {
        setTime();
        /**
         * 开始倒计时
         */
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        db = d;
                    }

                    @Override
                    public void onNext(Long num) {
                        if (timeTv != null && num > 0) {
                            timeTv.setText(getString(R.string.time_refresh, "" + num));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        clearTimeAdjust();
                        getTime();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearTimeAdjust();
    }

    private void clearTimeAdjust() {
        if (db != null && !db.isDisposed()) {
            db.dispose();
        }
    }

}
