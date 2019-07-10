package com.bankledger.safegem.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.version_tv)
    TextView versionTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setTitle(getString(R.string.about_us_str));
        versionTv.setText(getString(R.string.current_versin_str)+ Utils.getAppVersionName(this));
    }

    @OnClick({R.id.use_agre, R.id.safe_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.use_agre:
                HashMap<String, String> helpMap = new HashMap<>();
                helpMap.put(Constants.INTENT_DATA, getString(R.string.use_aggrement));
                helpMap.put(Constants.URL, getString(R.string.use_agreement_url));
                ActivitySkipUtil.skipAnotherActivity(AboutUsActivity.this, WebviewActivity.class, helpMap);
                break;
            case R.id.safe_img:

                break;
        }
    }
}
