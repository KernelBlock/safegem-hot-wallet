package com.bankledger.safegem.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bankledger.safegem.ui.activity.BaseActivity;

/**
 * 基础Fragment
 *
 * @author zhangmiao
 */
public abstract class BaseFragment extends Fragment {

    private View rootView = null;

    public View getRootView() {
        return rootView;
    }

    private Handler mHander = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(setContentView(), container, false);
        rootView.setClickable(true);
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 界面布局resId
     *
     * @return 布局resId
     */
    public abstract int setContentView();

    /**
     * 初始化界面
     */
    public abstract void initView();

    /**
     * 初始化界面数据
     */
    public abstract void initData();


    /**
     * 通过Id查找View， 为了和Activity统一。
     *
     * @param id
     * @return view
     */
    public <T extends View> T findViewById(int id) {
        if (getRootView() == null) {
            return null;
        }
        return rootView.findViewById(id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    public Handler getHander() {
        return mHander;
    }

    public boolean onBackPressed() {
        return false;
    }

    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }

}

