package com.bankledger.safegem.listener;

import java.io.File;

/**
 * Date：2018/10/22
 * Author: bankledger
 */
public interface OnDownloadListener {
    void onDownloadSuccess(File file);
    void onDownloadFail(Throwable throwable);
    void onProgress(int progress, long total);
}
