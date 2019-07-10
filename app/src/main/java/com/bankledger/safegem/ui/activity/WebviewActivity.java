package com.bankledger.safegem.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bankledger.safegem.R;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;

    private String title;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        title = getIntent().getStringExtra(Constants.INTENT_DATA);
        url = getIntent().getStringExtra(Constants.URL);
        setTitle(title);
        if (null != url) {
            webview.loadUrl(url);
        } else {
            finish();
        }
        webview.setWebViewClient(webViewClient);

        webview.removeJavascriptInterface("searchBoxJavaBridge_");
        webview.removeJavascriptInterface("accessibility");
        webview.removeJavascriptInterface("accessibilityTraversal");

    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            DialogUtil.dismissProgressDialog();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            DialogUtil.showProgressDialog(WebviewActivity.this);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

}
