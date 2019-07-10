package com.bankledger.safegem.utils;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.listener.OnDownloadListener;
import com.bankledger.safegem.net.download.DownloadApiManager;
import com.bankledger.safegem.net.download.DownloadClient;
import com.bankledger.safegem.net.download.FileDownLoadObserver;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;


import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * Date：2018/10/22
 * Author: bankledger
 */
public class DownloadUtil {
    /**
     * 下载
     */
    public static void downloadColdWallet(GetAppVersionResponse getAppVersionResponse, String file_name, OnDownloadListener onDownloadListener) {
        RingManager.getInstance().playCrystal();
        DownloadClient.initClient_BaseUrl(getAppVersionResponse.downUrl + "/");
        Disposable d = DownloadClient.getInstance()
                .downloadFile(getAppVersionResponse.downUrl, Constants.Companion.getCOLD_WALLET_PATH(), file_name + ".BSG",
                        new FileDownLoadObserver<File>() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.COLD_VERSION_MESSAGE, GsonUtils.toString(getAppVersionResponse));
                                onDownloadListener.onDownloadSuccess(file);
                            }

                            @Override
                            public void onDownloadFail(Throwable throwable) {
                                onDownloadListener.onDownloadFail(throwable);
                            }

                            @Override
                            public void onProgress(int progress, long total) {
                                onDownloadListener.onProgress(progress, total);
                            }
                        });
        DownloadApiManager.getInstance().add(d);
    }

}
