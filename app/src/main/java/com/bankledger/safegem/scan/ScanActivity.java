/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bankledger.safegem.scan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bankledger.protobuf.bean.CommonMsg;
import com.bankledger.protobuf.bean.TransAddress;
import com.bankledger.protobuf.bean.TransColdWalletInfo;
import com.bankledger.protobuf.bean.TransEos;
import com.bankledger.protobuf.bean.TransMulAddress;
import com.bankledger.protobuf.bean.TransSignTx;
import com.bankledger.protobuf.utils.ProtoUtils;
import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.net.model.request.AddColdWalletRequest;
import com.bankledger.safegem.qrcode.QRCodePage;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.scan.camera.CameraManager;
import com.bankledger.safegem.ui.activity.BaseActivity;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DPUtils;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.RingManager;
import com.bankledger.safegem.utils.StatusBarUtils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class ScanActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = ScanActivity.class.getSimpleName();
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title_view)
    RelativeLayout titleView;
    @BindView(R.id.title)
    TextView title_tv;
    @BindView(R.id.tip_tv)
    TextView tip_tv;
    @BindView(R.id.rv_scanned)
    RecyclerView rvScanned;

    private CameraManager cameraManager;
    private ScanActivityHandler handler;
    private Result savedResult;
    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private TextView scanHint;

    private boolean hasSurface = false;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;

    private InactivityTimer inactivityTimer;
    private AmbientLightManager ambientLightManager;
    private ArrayList<QRCodePage> pageList = new ArrayList<>();

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    private int flag = 0;
    private BaseRecyclerAdapter<QRCodePage> indicatorAdapter;
    private LinearLayoutManager layoutManager;
    private List<QRCodePage> indicatorList = new ArrayList<>();

    private boolean playBeep = true;
    private boolean vibrate = true;
    private static final long VIBRATE_DURATION = 300L;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        StatusBarUtils.setStatusBarTransparent(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        ambientLightManager = new AmbientLightManager(this);
        flag = getIntent().getIntExtra(Constants.SCAN_FLAG, -1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initScan();
    }

    private void initScan() {
        /**
         * 提示语初始化
         */
        String title=getIntent().getStringExtra(Constants.INTENT_KEY1);
        String tip_str=getIntent().getStringExtra(Constants.INTENT_KEY2);
        if (title != null){
            title_tv.setText(title);
        }
        if (tip_str != null){
            tip_tv.setVisibility(View.VISIBLE);
            tip_tv.setText(tip_str);
        }

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvScanned.setLayoutManager(layoutManager);
        indicatorAdapter = new BaseRecyclerAdapter<QRCodePage>(this, indicatorList, R.layout.listitem_scan_indicator) {
            int TYPE_NORMAL = 0;
            int TYPE_SCANNED = 1;

            @Override
            public int getItemViewType(int position) {
                if (pageList.contains(indicatorList.get(position))) {
                    return TYPE_SCANNED;
                } else {
                    return TYPE_NORMAL;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void convert(BaseRecyclerHolder holder, QRCodePage item, int position, boolean isScrolling) {
                TextView tvIndicator = holder.getView(R.id.tv_indicator);
                tvIndicator.setTextColor(getItemViewType(position) == TYPE_SCANNED ? Utils.getColor(R.color.scan_result_dots) : Utils.getColor(R.color.white));
                tvIndicator.setBackground(getItemViewType(position) == TYPE_SCANNED ? ContextCompat.getDrawable(ScanActivity.this, R.drawable.indicator_scanned_shape) : ContextCompat.getDrawable(ScanActivity.this, R.drawable.indicator_normal_shape));
                tvIndicator.setText(String.valueOf(item.getPageIndex() + 1));
            }
        };
        rvScanned.setAdapter(indicatorAdapter);
    }


    private void initIndicator() {
        if (indicatorList.size() == 0) {
            int total = pageList.get(0).getPageCount();
            for (int i = 0; i < total; i++) {
                QRCodePage page = new QRCodePage();
                page.setPageCount(total);
                page.setPageIndex(i);
                page.setContent("");
                indicatorList.add(page);
            }
            indicatorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initView() {
        super.initView();
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        scanHint = (TextView) findViewById(R.id.scan_hint);
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication());

        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        decodeFormats = null;
        characterSet = null;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        if (handler == null) {
            savedResult = result;
        } else {
            if (result != null) {
                savedResult = result;
            }
            if (savedResult != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResult);
                handler.sendMessage(message);
            }
            savedResult = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
        Rect frame = cameraManager.getFramingRect();
        if (frame != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) scanHint.getLayoutParams();
            lp.topMargin = frame.top - scanHint.getHeight() - DPUtils.dip2px(getBaseContext(), 16);
            scanHint.setVisibility(View.VISIBLE);
            scanHint.setLayoutParams(lp);
        }
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        if (playBeep) {
            RingManager.getInstance().playBeep();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }

        String content = rawResult.getText();
        decodeResult(content);
    }

    private void decodeResult(String content) {
        if (QRCodeUtil.getQRType(content) == QRCodeUtil.QRType.TYPE_QRCODE_PAGE) {
            QRCodePage page = QRCodePage.formatQrCodePage(content);
            if (!pageList.contains(page)) {
                if (pageList.size() > 0 && pageList.get(0).getPageCount() != page.getPageCount()) {
                    return;
                }
                if (page.getPageCount() > 999) {
                    ToastUtil.showShort(getActivity(), getString(R.string.code_err));
                    finish();
                    return;
                }
                pageList.add(page);
                initIndicator();
                indicatorList.remove(page.getPageIndex());
                indicatorList.add(page.getPageIndex(), page);
                indicatorAdapter.notifyDataSetChanged();
                if (QRCodeUtil.scanIsDone(pageList)) {
                    try {
                        String result = QRCodeUtil.decodePage(pageList);
                        onGetResult(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    restartPreviewAfterDelay(1000);
                }
            } else {
                restartPreviewAfterDelay(1000);
            }
            layoutManager.scrollToPositionWithOffset(page.getPageIndex() - 3 > 0 ? page.getPageIndex() - 3 : 0, 0);
        } else {
            onGetResult(content);
        }
    }

    private void onGetResult(String result) {
        if (flag == Constants.ADD_ADDRESS_BOOK) {
            Intent intent = new Intent();
            intent.putExtra(Constants.SCAN_DATA, result);
            setResult(Constants.ADD_ADDRESS_BOOK, intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(SafeGemApplication.getInstance().getColdUniqueId())) {
            if (flag != Constants.ADDWALLET && flag != Constants.ALL_SCAN) {
                ToastUtil.showShort(this, getString(R.string.wallet_no_tips));
                finish();
                return;
            }
        }
        CommonMsg msg = ProtoUtils.decodeCommonMsg(result);
        if (msg == null) {
            QRCodeUtil.QRType type = QRCodeUtil.getQRType(result);
            if (type == QRCodeUtil.QRType.TYPE_BLUETOOTH) {
                Intent intent = new Intent();
                intent.putExtra(Constants.SCAN_DATA, result);
                setResult(Constants.TYPE_UPDATE_WALLET, intent);
            } else {
                ToastUtil.showShort(this, getString(R.string.code_err));
            }
        } else if (msg != null && msg.isEnable()) { //协议可用
            String body = msg.body;
            if (flag == Constants.ADDWALLET && msg.getHeaderType() != ProtoUtils.HeaderType.TYPE_WALLET) {
                ToastUtil.showShort(this, getString(R.string.code_err));
            } else if (flag == Constants.ADD_ADDRESS && msg.getHeaderType() != ProtoUtils.HeaderType.TYPE_MONITOR) {
                ToastUtil.showShort(this, getString(R.string.code_err));
            } else if (flag == Constants.EOS_SCAN && msg.getHeaderType() != ProtoUtils.HeaderType.TYPE_EOS) {
                ToastUtil.showShort(this, getString(R.string.code_err));
            } else if (msg.getHeaderType() == ProtoUtils.HeaderType.TYPE_ADDR) {
                /**
                 * 同步余额
                 */
                TransAddress content = ProtoUtils.decodeAddress(body);
                if (content != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCAN_DATA, content);
                    setResult(Constants.TYPE_ADDR, intent);
                }
            } else if (msg.getHeaderType() == ProtoUtils.HeaderType.TYPE_MONITOR) {
                /**
                 *
                 添加监控地址
                 */
                TransMulAddress retTma = ProtoUtils.decodeMonitor(body);
                if (retTma != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCAN_DATA, retTma);
                    setResult(Constants.TYPE_MON, intent);
                }
            } else if (msg.getHeaderType() == ProtoUtils.HeaderType.TYPE_WALLET) {

                /**
                 *
                 绑定钱包
                 */
                TransColdWalletInfo retWalletInfo = ProtoUtils.decodeColdWalletInfo(body);
                AddColdWalletRequest addColdWalletRequest = new AddColdWalletRequest();
                addColdWalletRequest.coldUniqueId = retWalletInfo.walletSeqNumber;
                addColdWalletRequest.coldWalletName = retWalletInfo.walletName;
                addColdWalletRequest.bluetooth = Utils.decodeBase64(retWalletInfo.deviceName);
                if (retWalletInfo != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCAN_DATA, addColdWalletRequest);
                    setResult(Constants.TYPE_ADDWALLET, intent);
                }
            } else if (msg.getHeaderType() == ProtoUtils.HeaderType.TYPE_SIGN_TX) {

                /**
                 发送交易
                 */
                TransSignTx content = ProtoUtils.decodeSignTx(body);
                if (content != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCAN_DATA, content);
                    setResult(Constants.TYPE_SEND_TX, intent);
                }
            } else if (msg.getHeaderType() == ProtoUtils.HeaderType.TYPE_EOS) {

                /**
                 传输Eos
                 */
                TransEos transEos = ProtoUtils.decodeEos(body);
                transEos.deviceName = Utils.decodeBase64(transEos.deviceName);
                if (transEos != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCAN_DATA, transEos);
                    setResult(Constants.TYPE_EOS, intent);
                }
            }else {
                ToastUtil.showShort(getActivity(), getString(R.string.code_err));
            }
        } else if (msg.checkLocalProtocolUpdate()) {
            //提示需要更新自己APP
            ToastUtil.showShort(getActivity(), R.string.hint_hot_wallet_update);
        } else {
            //提示需要更新外部APP
            ToastUtil.showShort(getActivity(), R.string.hint_cold_wallet_update);
        }
        finish();
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new ScanActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.hint_camera_framework_bug));
        builder.setPositiveButton(R.string.ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


}
