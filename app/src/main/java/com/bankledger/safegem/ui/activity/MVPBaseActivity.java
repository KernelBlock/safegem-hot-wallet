package com.bankledger.safegem.ui.activity;

import android.os.Bundle;

import com.bankledger.safegem.presenter.BasePresenter;
import com.bankledger.safegem.view.IBaseLifecycleView;
import com.trello.rxlifecycle2.LifecycleTransformer;

import butterknife.Unbinder;

/**
 * Date：2018/7/30
 * Author: bankledger
 */
public abstract class MVPBaseActivity<V, T extends BasePresenter<V>> extends BaseActivity implements IBaseLifecycleView {
    protected T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建Presenter
        mPresenter = createPresenter();
        //关联View
        mPresenter.attachView((V) this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //接触关联
        mPresenter.detachView();
    }

    protected abstract T createPresenter();

    @Override
    public LifecycleTransformer bindLifecycle() {
        return bindToLifecycle();
    }
}
