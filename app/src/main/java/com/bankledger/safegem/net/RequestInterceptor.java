package com.bankledger.safegem.net;

import com.bankledger.safegem.utils.Constants;


import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        request = requestBuilder
                .addHeader("channel", "android")
                .addHeader("language", Constants.Companion.getLANGUAGE_HEADER())
                .addHeader("Content-Type","application/json;charset=UTF-8")
                .addHeader("Authorization", Constants.Companion.getAUTHORIZATION_HEADER())
                .build();
        return chain.proceed(request);
    }

}
