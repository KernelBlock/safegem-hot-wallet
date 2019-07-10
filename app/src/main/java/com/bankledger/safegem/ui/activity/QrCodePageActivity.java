package com.bankledger.safegem.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.adapter.ViewPagerStateAdapter;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.ui.fragment.QrCodeFragment;
import com.bankledger.safegem.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2018/7/4.
 */

public class QrCodePageActivity extends BaseActivity {

    private ViewPager mVpView;
    private TextView mTvPage;
    private Button mBtnPrev;
    private Button mBtnNext;

    private List<Fragment> mFgtList = new ArrayList<>();
    private ViewPagerStateAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_page);
    }

    @Override
    public void initView() {
        super.initView();
        mVpView = findViewById(R.id.vp_view);
        mTvPage = findViewById(R.id.tv_page);
        mBtnPrev = findViewById(R.id.btn_prev);
        mBtnNext = findViewById(R.id.btn_next);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        String content = intent.getStringExtra(Constants.INTENT_KEY1);
        adapter = new ViewPagerStateAdapter(getSupportFragmentManager());
        mVpView.setAdapter(adapter);
        List<String> mList = QRCodeUtil.encodePage(content);
        for (String item : mList){
            QrCodeFragment fragment = new QrCodeFragment();
            Bundle args = new Bundle();
            args.putString(Constants.INTENT_KEY1, item);
            fragment.setArguments(args);
            mFgtList.add(fragment);
        }
        adapter.setFragment(mFgtList);
        setTvPage(mVpView.getCurrentItem());
        mVpView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTvPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mVpView.getCurrentItem();
                mVpView.setCurrentItem(--position);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mVpView.getCurrentItem();
                mVpView.setCurrentItem(++position);
            }
        });
    }

    public void setTvPage(int position) {
        mTvPage.setText(++position + "/" + mFgtList.size());
    }

}
