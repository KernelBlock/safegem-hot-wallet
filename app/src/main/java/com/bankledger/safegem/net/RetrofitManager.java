package com.bankledger.safegem.net;

import com.bankledger.safegem.utils.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * 内部服务器接口请求单例
 * Date：2018/8/21
 * Author: bankledger
 */
public class RetrofitManager {

    private Retrofit retrofit;
    public NetService mNetService;
    private static volatile RetrofitManager INSTANCE;
    private static final long TIME_OUT = 40;

    private RetrofitManager(){
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new RequestInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.isDebug ? Constants.NET_BASE_URL_DEBUG:Constants.NET_BASE_URL_RELEASE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MyGsonConverterFactory.create())
                .client(client)
                .build();
        mNetService = retrofit.create(NetService.class);
    }


    public static void clearInstance(){
        INSTANCE = null;
    }

    public static RetrofitManager getInstance(){
        if(INSTANCE == null){
            synchronized (RetrofitManager.class){
                if(INSTANCE == null){
                    INSTANCE =  new RetrofitManager();
                }
            }
        }
        return INSTANCE;
    }

}
