package com.bankledger.safegem.net.download;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Date：2018/8/13
 * Author: bankledger
 */
public class DownloadApiManager {

    private static DownloadApiManager instance = null;

    //请求队列
    private CompositeDisposable compositeDisposable;

    public static DownloadApiManager getInstance() {
        if (instance == null) {
            synchronized (DownloadApiManager.class) {
                if (instance == null) {
                    instance = new DownloadApiManager();
                }
            }
        }
        return instance;
    }

    private DownloadApiManager() {
        try {
            compositeDisposable = new CompositeDisposable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

}
