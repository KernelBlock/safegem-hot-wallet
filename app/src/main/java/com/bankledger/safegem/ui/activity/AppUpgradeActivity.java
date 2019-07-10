package com.bankledger.safegem.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bankledger.FileEncrypt;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.bluetooth.BluetoothSPP;
import com.bankledger.safegem.bluetooth.BluetoothState;
import com.bankledger.safegem.listener.OnDownloadListener;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.scan.ScanActivity;
import com.bankledger.safegem.ui.widget.CommonDialog;
import com.bankledger.safegem.utils.Base64;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;

import com.bankledger.safegem.utils.DownloadUtil;
import com.bankledger.safegem.utils.MD5Utils;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.utils.IoUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AppUpgradeActivity extends BaseActivity {

    private ImageView ivBlue;
    private TextView tvState;
    private ProgressBar pbPlan;
    private TextView tvPlan;
    private AnimationDrawable bluAnim;
    private String blueAddress;
    private String coldVersion;
    private int zipVersion = 4;
    private BluetoothSPP bluetooth;
    public static String QR_BLUETOOTH = "*BLUETOOTH:";
    private String mBeginFlag = "HEADER:";
    private String mEndFlag = "END";
    private String mFileFinish = "FILE_FINISH";
    private Handler mHandler = new Handler();
    private int downloadProgress = 1;
    private GetAppVersionResponse getAppVersionResponse;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_upgrade);
    }

    @Override
    public void initView() {
        super.initView();
        setTitle(R.string.cold_wallet_update);
        setDefaultNavigation();
        setNavigation(R.drawable.ic_keyboard_arrow_left_white_30dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivBlue = findViewById(R.id.iv_blue);
        bluAnim = (AnimationDrawable) ivBlue.getDrawable();
        bluAnim.start();

        tvState = findViewById(R.id.tv_state);
        pbPlan = findViewById(R.id.pb_plan);
        tvPlan = findViewById(R.id.tv_plan);

    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        String bluetoothInfo = intent.getStringExtra(Constants.INTENT_KEY1);
        getAppVersionResponse= (GetAppVersionResponse) intent.getSerializableExtra(Constants.INTENT_KEY2);
        int beginIndex = QR_BLUETOOTH.length();
        int endIndex = bluetoothInfo.length();
        String walletInfo = bluetoothInfo.substring(beginIndex, endIndex);
        String[] wallets = walletInfo.split("\\|");

        if (wallets.length == 2) {
            blueAddress = new String(Base64.decode(wallets[0], Base64.DEFAULT));
            coldVersion = wallets[1];
        }

        bluetooth = new BluetoothSPP(this);
        if (!bluetooth.isBluetoothAvailable()) {
            finish();
        }

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                if (message.equals(mFileFinish)) {
                    ToastUtil.showShort(getActivity(), R.string.cold_wallet_update_success);
                    //删除冷钱包文件
                    String path = Constants.Companion.getCOLD_WALLET_PATH()+ File.separator + Constants.Companion.getCOLD_WALLET_FILE_NAME() + ".apk";
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    finish();
                }
            }
        });
        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                tvState.setText(R.string.cold_wallet_bluetooth_disconnected);
                showDisconnectedDialog();
            }

            public void onDeviceConnectionFailed() {
                if (!isFinishing()) {
                    tvState.setText(R.string.cold_wallet_bluetooth_connection_failure);
                }
            }

            public void onDeviceConnected(String name, String address) {
                tvState.setText(R.string.dowload_tip);
                DownloadUtil.downloadColdWallet(getAppVersionResponse, Constants.Companion.getCOLD_WALLET_FILE_NAME(),new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        tvState.setText(R.string.cold_wallet_bluetooth_connected);
                        downloadProgress = 100;
                        pbPlan.setProgress(downloadProgress);
                        int showProgress = (int) ((pbPlan.getProgress() * 1.0f / pbPlan.getMax()) * 100);
                        tvPlan.setText(showProgress + "%");
                        sendMessage();
                    }

                    @Override
                    public void onDownloadFail(Throwable throwable) {
                        final CommonDialog dialog = new CommonDialog(getActivity());
                        dialog.setTitle(R.string.hint_str);
                        dialog.setContentText(getString(R.string.cold_wallet_dowolod_fail));
                        dialog.setCancelable(false);
                        dialog.setOnClickCloseListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        dialog.setOnClickConfirmListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        dialog.show();
                    }

                    @Override
                    public void onProgress(int progress, long total) {
                        mHandler.post(new Runnable() { //下载进度在子线程
                            @Override
                            public void run() {
                                downloadProgress = progress;
                                pbPlan.setProgress(downloadProgress);
                                int showProgress = (int) ((pbPlan.getProgress() * 1.0f / pbPlan.getMax()) * 100);
                                tvPlan.setText(showProgress + "%");
                            }
                        });
                    }
                });

            }
        });
    }

    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bluetooth.isServiceAvailable()) {
                startConnect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetooth != null) {
            bluetooth.stopService();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startConnect();
            } else {
                finish();
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            bluetooth.setupService();
            bluetooth.startService(BluetoothState.DEVICE_ANDROID);
            bluetooth.connect(blueAddress);
        }
    };

    public void startConnect() {
        tvState.setText(R.string.cold_wallet_connecting_bluetooth);
        mHandler.postDelayed(mRunnable, 2000);

    }

    public void sendMessage() {
        String md5 = SafeGemApplication.getInstance().getColdVersionMessage().md5;
        String path = Constants.Companion.getCOLD_WALLET_PATH() + File.separator + Constants.Companion.getCOLD_WALLET_FILE_NAME() + ".BSG";
        if (!FileEncrypt.checkFile(path, md5)) {
            ToastUtil.showShort(getActivity(), getString(R.string.cold_wallet_md5_not_correct));
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            finish();
        } else {
            if (Integer.parseInt(coldVersion) < zipVersion) {
                path = FileEncrypt.decodeFile(path, "YinLianLauncher.apk", md5);
            }
            File file = new File(path);
            long fileSize = file.length();
            Observable.create((ObservableOnSubscribe<Long>) e -> {
                FileInputStream fis = new FileInputStream(file);
                bluetooth.send(mBeginFlag + md5 + "|" + fis.available(), true);
                int length;
                long cursor = 0;
                byte[] buffer = new byte[1000];
                while ((length = fis.read(buffer)) != -1) {
                    if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                        cursor += length;
                        if (length < 1000) {
                            byte[] dest = new byte[length];
                            System.arraycopy(buffer, 0, dest, 0, length);
                            bluetooth.send(Base64.encodeToString(dest, Base64.DEFAULT), true);
                        } else {
                            bluetooth.send(Base64.encodeToString(buffer, Base64.DEFAULT), true);
                        }
                        e.onNext(cursor);
                    } else {
                        break;
                    }
                }
                bluetooth.send(mEndFlag, true);
                IoUtils.closeQuietly(fis);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long cursor) {
                            int progress = (int) ((cursor * 1.0f / fileSize) * 100);
                            if (progress <= 0) {
                                progress = 1;
                            }
                            int realProgress = progress;
                            pbPlan.setProgress(realProgress);

                            int showProgress = (int) ((pbPlan.getProgress() * 1.0f / pbPlan.getMax()) * 100);
                            tvPlan.setText(showProgress + "%");
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showTextDialog(this, R.string.cold_wallet_exit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showDisconnectedDialog() {
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setTitle(R.string.hint_str);
        dialog.setContentText(getString(R.string.cold_wallet_retry_update));
        dialog.setCancelable(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.show();
    }

}
