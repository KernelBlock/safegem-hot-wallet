package com.bankledger.safegem.net;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.ui.activity.LoginRegGuideActivity;
import com.bankledger.safegem.utils.ToastUtil;
import com.bankledger.safegem.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Date：2018/8/23
 * Author: bankledger
 */
public class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final String SUCCESS_CODE = "0";
    private static final String TOKEN_EXPIRE = "30000";
    private static final String MESSAGE_HIDE = "19999";

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public ResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String json = value.string();
        try {
            if (verify(json)) {
                return adapter.read(gson.newJsonReader(new StringReader(json)));
            } else {
                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                String messages = jsonObject.get("message").getAsString();
                throw new IOException(messages);
            }
        } finally {
            value.close();
        }
    }

    private boolean verify(String json) {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        String code = jsonObject.get("code").getAsString();
        String msg = jsonObject.get("message").getAsString();
        if (code.equals(SUCCESS_CODE)) {
            return true;
        } else {
            Message message = new Message();
            if (code.equals(TOKEN_EXPIRE)) {
                message.what = 0;
            } else if (code.equals(MESSAGE_HIDE)) {
                message.what = 1;
            } else {
                message.what = 2;
            }
            message.obj = msg;
            handler.sendMessage(message);
            return false;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Utils.clearTableData();
                    Intent intent = new Intent();
                    intent.setClass(SafeGemApplication.getInstance(), LoginRegGuideActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SafeGemApplication.getInstance().startActivity(intent);
                    ToastUtil.showShort(msg.obj.toString());
                    break;
                case 1:
                    break;
                case 2:
                    try {
                        ToastUtil.showShort(msg.obj.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

}
