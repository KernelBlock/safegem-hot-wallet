package com.bankledger.safegem.ui.activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.BaseRecyclerAdapter;
import com.bankledger.safegem.adapter.BaseRecyclerHolder;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.utils.ActivitySkipUtil;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DialogUtil;
import com.bankledger.safegem.utils.LanguageConstants;
import com.bankledger.safegem.utils.LanguageUtil;
import com.bankledger.safegem.utils.LogUtils;


import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class LanguageSelectActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<String> list;
    private BaseRecyclerAdapter<String> mAdapter;
    private int currentIndex;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);
        setTitle(R.string.lange_str);
        currentLanguage = SafeGemApplication.getInstance().appSharedPrefUtil.get(Constants.LANGUAGE, "");
        if(TextUtils.isEmpty(currentLanguage)){
            currentLanguage = LanguageUtil.getLanguageEnv();
        }
        if(currentLanguage.equals(LanguageConstants.SIMPLIFIED_CHINESE)){
            currentIndex = 1;
        } else if(currentLanguage.equals(LanguageConstants.TRADITIONAL_CHINESE)){
            currentIndex = 2;
        } else  {
            currentIndex = 0;
        }
        setRightText(getString(R.string.save_str), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex == 1) {
                    currentLanguage = LanguageConstants.SIMPLIFIED_CHINESE;
                } else if (currentIndex == 2) {
                    currentLanguage = LanguageConstants.TRADITIONAL_CHINESE;
                } else {
                    currentLanguage = LanguageConstants.ENGLISH;
                }
                SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.LANGUAGE, currentLanguage);
                Constants.Companion.setLANGUAGE_HEADER(currentLanguage);
                setResult(RESULT_OK);
                finish();
            }
        });
        Resources resources = getResources();
        list = Arrays.asList(resources.getStringArray(R.array.language_arr));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<String>(this, list, R.layout.language_item_layout) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                if (currentIndex != -1) {
                    if (position == currentIndex) {
                        holder.getView(R.id.lang_img).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.lang_img).setVisibility(View.GONE);
                    }
                    holder.setText(R.id.name, item);
                }
            }
        };

        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                currentIndex = position;
                mAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

}
