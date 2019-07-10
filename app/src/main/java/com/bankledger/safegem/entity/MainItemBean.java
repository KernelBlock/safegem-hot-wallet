package com.bankledger.safegem.entity;

import android.app.Activity;

/**
 * Date：2018/8/1
 * Author: bankledger
 */
public class MainItemBean {

    private int imgId;
    private String mainName;
    private Class<? extends Activity> cls = null;

    public MainItemBean(int imgId, String mainName) {
        this.imgId = imgId;
        this.mainName = mainName;
    }

    public MainItemBean(int imgId, String mainName, Class<? extends Activity> cls) {
        this.imgId = imgId;
        this.mainName = mainName;
        this.cls = cls;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public Class<? extends Activity> getCls() {
        return cls;
    }

    public void setCls(Class<? extends Activity> cls) {
        this.cls = cls;
    }


}
