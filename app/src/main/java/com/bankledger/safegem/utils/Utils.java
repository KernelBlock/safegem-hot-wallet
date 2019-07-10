/**
 * Copyright 2011 Google Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bankledger.safegem.utils;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.protobuf.bean.EthToken;
import com.bankledger.protobuf.bean.TransBalance;
import com.bankledger.protobuf.utils.ProtoUtils;
import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.DaoManager;
import com.bankledger.safegem.greendaodb.dao.AddressBookTbDao;
import com.bankledger.safegem.greendaodb.dao.ColdWalletTbDao;
import com.bankledger.safegem.greendaodb.dao.EosAccountTbDao;
import com.bankledger.safegem.greendaodb.dao.EosBalanceTbDao;
import com.bankledger.safegem.greendaodb.dao.EosTxTbDao;
import com.bankledger.safegem.greendaodb.dao.EthTokenTbDao;
import com.bankledger.safegem.greendaodb.dao.MessageTbDao;
import com.bankledger.safegem.greendaodb.dao.MonitorAddressTbDao;
import com.bankledger.safegem.greendaodb.dao.TxInsTbDao;
import com.bankledger.safegem.greendaodb.dao.TxOutsTbDao;
import com.bankledger.safegem.greendaodb.dao.TxTbDao;
import com.bankledger.safegem.greendaodb.dao.UsdtTxTbDao;
import com.bankledger.safegem.greendaodb.dao.UserTbDao;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.net.model.request.TransactionListRequest;
import com.bankledger.safegem.net.model.response.ERCResponse;
import com.bankledger.safegem.service.UpdateBalanceService;
import com.bankledger.safegem.ui.activity.AppUpgradeActivity;
import com.bankledger.safegem.ui.activity.BaseActivity;
import com.bankledger.safegem.ui.view.QRCodeWindow;


import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class Utils {

    private static long lastClickTime;

    private static final int MIN_CLICK_DELAY_TIME = 500;

    public static boolean isNubmer(Object obj) {
        try {
            Double.parseDouble(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteger(Object obj) {
        try {
            Integer.parseInt(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isLong(Object obj) {
        try {
            Long.parseLong(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String getRandom() {
        String str = "";
        for (int i = 0; i <= 100; i++) {
            String sources = "0123456789";
            Random rand = new Random();
            StringBuffer flag = new StringBuffer();
            for (int j = 0; j < 6; j++) {
                flag.append(sources.charAt(rand.nextInt(9)) + "");
            }
            str = flag.toString();
        }
        return str;
    }

    // Base64加密
    public static String getBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // Base64加密
    public static String decodeBase64(String str) {
        String result = "";
        if (str != null) {
            result = new String(Base64.decode(str, Base64.NO_PADDING));
        }
        return result;
    }

    public static int getColor(int colorId) {
        return ContextCompat.getColor(SafeGemApplication.getInstance(), colorId);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // check all network connect, WIFI or mobile
    public static boolean isNetworkAvailable() {
        boolean hasWifiCon = false;
        boolean hasMobileCon = false;
        ConnectivityManager cm = (ConnectivityManager) SafeGemApplication.getInstance().getSystemService(SafeGemApplication.getInstance().CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {
            String type = net.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                if (net.isConnected()) {
                    hasWifiCon = true;
                }
            }
            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
                }
            }
        }
        return hasWifiCon || hasMobileCon;
    }

    public static String formatActiveEthToken(String address, ERCResponse.ERC erc) {
        //传输激活ERC20
        EthToken ethToken = new EthToken();
        ethToken.name = erc.getName();
        ethToken.symbol = erc.getSymbol();
        ethToken.decimals = Integer.parseInt(erc.getDecimals());
        ethToken.totalSupply = erc.getTotalSupply();
        ethToken.contractsAddress = erc.getContractAddress();
        ethToken.ethAddress = address;
        String encodeEthToken = ProtoUtils.encodeActiveERC20(ethToken);
        return encodeEthToken;
    }

    public static int getCoinImg(String name, int coinType) {
        int img = 0;
        if (TextUtils.isEmpty(name)) {
            return img;
        }
        if (coinType == Constants.COIN_BTC) {
            if (name.equals(Constants.BCH)) {
                img = R.drawable.bch_icon;
            } else if (name.equals(Constants.BCHABC)) {
                img = R.drawable.bch_icon;
            } else if (name.equals(Constants.BSV)) {
                img = R.drawable.bsv_icon;
            } else if (name.equals(Constants.BCHSV)) {
                img = R.drawable.bsv_icon;
            } else if (name.equals(Constants.BTC)) {
                img = R.drawable.btc_img;
            } else if (name.equals(Constants.DASH)) {
                img = R.drawable.dash_img;
            } else if (name.equals(Constants.LTC)) {
                img = R.drawable.ltc_img;
            } else if (name.equals(Constants.QTUM)) {
                img = R.drawable.qtum_img;
            } else if (name.equals(Constants.BTG)) {
                img = R.drawable.btg_coin;
            } else if (name.equals(Constants.FTO)) {
                img = R.drawable.fto_icon;
            } else if (name.equals(Constants.SAFE)) {
                img = R.drawable.safe_img;
            } else {
                img = 0;
            }
        } else if (coinType == Constants.COIN_ETH || coinType == Constants.COIN_ETH_TOKEN) {
            img = R.drawable.eth_img;
        } else if (coinType == Constants.COIN_ETC) {
            img = R.drawable.etc_icon;
        } else if (coinType == Constants.COIN_EOS || coinType == Constants.COIN_EOS_TOKEN) {
            img = R.drawable.eos_icon;
        } else if (coinType == Constants.COIN_USDT) {
            img = R.drawable.logo_usdt;
        } else if (coinType == Constants.COIN_SAFE_ASSET) {
            img = R.drawable.safe_img;
        }
        return img;
    }

    public static String getCodeContent(String name, String address, String amount, int coinType) {
        StringBuilder mBuilder = new StringBuilder();
        String fullName;
        String tokenName = null;
        if (coinType == Constants.COIN_ETH_TOKEN) {
            fullName = Constants.ETH_FULL_NAME;
            tokenName = name;
        } else if (coinType == Constants.COIN_EOS_TOKEN) {
            fullName = Constants.EOS_FULL_NAME;
            tokenName = name;
        } else if (coinType == Constants.COIN_SAFE_ASSET) {
            fullName = Constants.SAFE_FULL_NAME;
            tokenName = name;
        } else {
            fullName = name;
        }
        mBuilder.append(fullName);
        mBuilder.append(":");
        mBuilder.append(address);
        mBuilder.append("?amount=");
        mBuilder.append(amount);
        if (!TextUtils.isEmpty(tokenName)) {
            mBuilder.append("&token=");
            mBuilder.append(tokenName);
        }
        return mBuilder.toString();
    }

    public static void clearTableData() {
        try {
            SafeGemApplication.getInstance().appSharedPrefUtil.putInt(Constants.CURRENT_WALLET_POSITION, -1);
            SafeGemApplication.getInstance().appSharedPrefUtil.put(Constants.CURRENT_WALLET_ID, null);

            DaoManager mManager = DaoManager.getInstance();
            mManager.init(SafeGemApplication.getInstance());
            Database database = mManager.getDaoSession().getDatabase();
            String wheres[] = new String[]{String.valueOf(SafeGemApplication.getInstance().getUserInfo().getUserId())};
            /**
             * 用户信息
             */
            String sql = "DELETE FROM " + UserTbDao.TABLENAME + " where user_id = ?";
            database.execSQL(sql, wheres);

            /**
             * 冷钱包信息
             */
            sql = "DELETE FROM " + ColdWalletTbDao.TABLENAME + " where user_id = ?";
            database.execSQL(sql, wheres);

            /**
             * 地址薄信息
             */
            sql = "DELETE FROM " + AddressBookTbDao.TABLENAME + " where user_id = ?";
            database.execSQL(sql, wheres);

            /**
             * 监控地址信息
             */
            sql = "DELETE FROM " + MonitorAddressTbDao.TABLENAME + " where user_id = ?";
            database.execSQL(sql, wheres);

            /**
             * 消息信息
             */
            sql = "DELETE FROM " + MessageTbDao.TABLENAME + " where user_id = ?";
            database.execSQL(sql, wheres);

            /**
             * 交易信息
             */
            sql = "DELETE FROM " + TxTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * 交易输入信息
             */
            sql = "DELETE FROM " + TxInsTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * 交易输出信息
             */
            sql = "DELETE FROM " + TxOutsTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * ERCTOKEN信息
             */
            sql = "DELETE FROM " + EthTokenTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * EOS账户信息
             */
            sql = "DELETE FROM " + EosAccountTbDao.TABLENAME;
            database.execSQL(sql);


            /**
             * EOS余额账户
             */
            sql = "DELETE FROM " + EosBalanceTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * EOS交易信息
             */
            sql = "DELETE FROM " + EosTxTbDao.TABLENAME;
            database.execSQL(sql);

            /**
             * Usdt交易信息
             */
            sql = "DELETE FROM " + UsdtTxTbDao.TABLENAME;
            database.execSQL(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
        SafeGemApplication.getInstance().clearUserInfo();
        SafeGemApplication.getInstance().stopService(new Intent(SafeGemApplication.getInstance(), UpdateBalanceService.class));
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(versioncode);
    }


    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void copyContent(String content, Context context) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", content);
        myClipboard.setPrimaryClip(myClip);
        ToastUtil.showShort(context, R.string.copy_success);
    }

    public static QRCodeWindow showQrCodeCotent(BaseActivity activity, String coldUniqueId, List<CoinBalance> coinBalanceList) {
        if (coinBalanceList != null && coinBalanceList.size() > 0) {
            TransBalance tb = new TransBalance(coldUniqueId, coinBalanceList);
            String content = ProtoUtils.encodeBalance(tb);
            QRCodeWindow qrCode = new QRCodeWindow(activity, content);
            qrCode.show(activity);
            return qrCode;
        } else {
            ToastUtil.showShort(activity, R.string.no_update_tip);
            return null;
        }
    }

    /**
     * 安装新版本应用
     */
    public static void installApp(File apkFile, String authority, Activity mContext) {
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(mContext, authority, apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            //兼容8.0
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    //请求安装未知应用来源的权限
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 6666);
                }
            }
        } else {
            // 通过Intent安装APK文件
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                    "application/vnd.android.package-archive");
        }
        if (mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            mContext.startActivity(intent);
        }

    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, List<MonitorAddressTb>> sortMapByKey(Map<String, List<MonitorAddressTb>> map) {
        if (map == null || map.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, List<MonitorAddressTb>> sortMap = new TreeMap<String, List<MonitorAddressTb>>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return s.compareTo(t1);
                    }
                });
        sortMap.putAll(map);
        return sortMap;
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getColdVersion(String bluetoothInfo) {
        int beginIndex = AppUpgradeActivity.QR_BLUETOOTH.length();
        int endIndex = bluetoothInfo.length();
        String walletInfo = bluetoothInfo.substring(beginIndex, endIndex);
        String[] wallets = walletInfo.split("\\|");
        String coldVersion = "";
        if (wallets.length == 2) {
            coldVersion = wallets[1];
        }
        return coldVersion;
    }

    public static int coin2Type(String coin) {
        int coinType;
        if (coin.equals(Constants.ETH)) {
            coinType = Constants.COIN_ETH;
        } else if (coin.equals(Constants.ETC)) {
            coinType = Constants.COIN_ETC;
        } else if (coin.equals(Constants.EOS)) {
            coinType = Constants.COIN_EOS;
        } else if (coin.equals(Constants.USDT)) {
            coinType = Constants.COIN_USDT;
        } else {
            coinType = Constants.COIN_BTC;
        }
        return coinType;
    }

    public static boolean isRepeatClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
            lastClickTime = curClickTime;
        }
        return flag;
    }

    public static List<TransactionListRequest.AddressMessage> getAddressMessageList(List<String> addressList) {
        List<TransactionListRequest.AddressMessage> addressMessageList = new ArrayList<>();
        for (String item : addressList) {
            addressMessageList.add(new TransactionListRequest.AddressMessage(item, SafeGemApplication.getInstance().lastTxId(item)));
        }
        return addressMessageList;
    }

}
