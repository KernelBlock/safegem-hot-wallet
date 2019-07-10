package com.bankledger.safegem.view;

import com.bankledger.safegem.entity.MainItemBean;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.net.model.response.GetAppVersionResponse;

import java.io.File;
import java.util.List;

/**
 * Date：2018/7/30
 * Author: bankledger
 */
public interface IMainView extends IBaseLifecycleView {

    void onMainDataComplete(List<MainItemBean> datas);

    void onSendTxComplete(String hex);

    void onShowDialog(String msg);

    void onAddColdWalletComplete(ColdWalletTb coldWallet);

    void onRemoveColdWalletComplete(int position);

    void onUpdateColdWalletComplete(String id,String name);

    void onGetMessageComplete();

    void onHasNewVersion(GetAppVersionResponse getAppVersionResponse,int productType);

    void onNoNewVersion(int productType);

    void onDownloadSuccess(File file);

    void onDownloadFail(Throwable throwable);

    void onProgress(int progress, long total);

    void onGetSingComplete(String singContent);

}
