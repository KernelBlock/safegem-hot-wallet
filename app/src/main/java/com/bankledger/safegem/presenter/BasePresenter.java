package com.bankledger.safegem.presenter;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Date：2018/7/30
 * Author: bankledger
 */
public class BasePresenter<T> {

    /**
     * 当内存不足释放内存
     */
    protected WeakReference<T> mViewRef; // view 的弱引用

    /**
     * bind p with v
     * @param view
     */
    public void attachView(T view){
        mViewRef = new WeakReference<T>(view);
    }

    public void detachView(){
        if (mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }
    /**
     * 获取view的方法
     *
     * @return 当前关联的view
     */
    public T getView() {
        if (mViewRef != null){
            return mViewRef.get();
        }
       return null;
    }

}
