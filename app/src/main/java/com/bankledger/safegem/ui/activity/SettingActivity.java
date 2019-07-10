package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.LanguageConstants;
import com.bankledger.safegem.utils.LanguageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.lang_tv)
    TextView langTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle(getString(R.string.setting_str));
        String language = SafeGemApplication.getInstance().appSharedPrefUtil.get(Constants.LANGUAGE, "");
        String[] arrays = getResources().getStringArray(R.array.language_arr);
        if(TextUtils.isEmpty(language)){
            language = LanguageUtil.getLanguageEnv();
        }
        if (language.equals(LanguageConstants.SIMPLIFIED_CHINESE)) {
            langTv.setText(arrays[1]);
        } else if (language.equals(LanguageConstants.TRADITIONAL_CHINESE)) {
            langTv.setText(arrays[2]);
        } else {
            langTv.setText(arrays[0]);
        }
    }

    @OnClick({R.id.lange_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lange_view:
                startActivityForResult(new Intent(SettingActivity.this, LanguageSelectActivity.class), 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(Constants.TYPE_LANGUAGE);
            finish();
        }
    }

}
