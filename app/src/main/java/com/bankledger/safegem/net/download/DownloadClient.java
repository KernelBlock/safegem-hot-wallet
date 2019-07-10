package com.bankledger.safegem.net.download;

import com.bankledger.safegem.net.NetService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date：2018/8/13
 * Author: bankledger
 */
public class DownloadClient {

    private static DownloadClient mInstance;
    private static Retrofit retrofit;
    private static String mBaseUrl;
    private DownloadClient(String baseUrl){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 配置自定义的OkHttpClient
     */
    public static DownloadClient initClient_BaseUrl( @NonNull String baseUrl){
        mBaseUrl = baseUrl;
        if (mInstance==null){
            synchronized (DownloadClient.class){
                if (mInstance==null){
                    mInstance = new DownloadClient(baseUrl);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取Retrofit的实例
     */
    public static DownloadClient getInstance(){
        if (mBaseUrl == null) {
            throw new RuntimeException("Please initialize Your \"BaseUrl\" in Application before use");
        }
        if (mInstance == null) {
            throw new RuntimeException("Please initialize Your \"RetrofitClient\" in Application before use");
        }
        return mInstance;
    }

    public <T> T create(Class<T> clz){
        return retrofit.create(clz);
    }

    /**
     * 下载单文件
     * @param url                  文件地址
     * @param destDir              存储文件夹
     * @param fileName             存储文件名
     * @param fileDownLoadObserver 监听回调
     */
    public Disposable downloadFile(@NonNull String url, final String destDir, final String fileName,
                                   final FileDownLoadObserver<File> fileDownLoadObserver){
        return create(NetService.class)
                .downLoadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return fileDownLoadObserver.saveFile(responseBody,destDir,fileName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        fileDownLoadObserver.onDownloadSuccess(file);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        fileDownLoadObserver.onDownloadFail(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        fileDownLoadObserver.onComplete();
                    }
                });
    }
}
