package com.bankledger.safegem.qrcode;

import com.bankledger.protobuf.bean.TransSignTx;
import com.bankledger.safegem.net.model.request.SendTransactionRequest;
import com.bankledger.safegem.ui.activity.AppUpgradeActivity;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.Utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 二维码传输协议
 *
 * @author zm
 */
public class QRCodeUtil {

    public static final String QR_CODE_COLON = ":";
    public static final String QR_CODE_SPLIT = "/";
    public static final String QR_CODE_PAGE_SPLIT = "@";

    public static String[] split(String text) {
        return text.split(QR_CODE_SPLIT);
    }

    public static String[] splitPage(String text) {
        return text.split(QR_CODE_PAGE_SPLIT);
    }

    /**
     * 发送交易
     * @return
     */
    public static SendTransactionRequest decodeSendTrade(TransSignTx content) {
        SendTransactionRequest sendTransactionRequest = new SendTransactionRequest();
        sendTransactionRequest.coin = content.signTx.coin;
        sendTransactionRequest.hex = content.signTx.txHex;
        return sendTransactionRequest;
    }


    public static List<String> encodePage(String text) {
        List<String> pageList = new ArrayList<String>();
        int num = getNumOfQrCodeString(text.length());
        int pageSize = QRQuality.Normal.getQuality();
        for (int i = 0; i < num; i++) {
            int start = i * pageSize;
            int end = (i + 1) * pageSize;
            if (end > text.length()) {
                end = text.length();
            }
            String splitStr = text.substring(start, end);
            String pageString = "";
            pageString = Integer.toString(num - 1) + QR_CODE_PAGE_SPLIT + Integer.toString(i) + QR_CODE_PAGE_SPLIT;
            pageList.add(pageString + splitStr);
        }
        return pageList;
    }

    public static String decodePage(List<QRCodePage> pageList) throws Exception {
        List<QRCodePage> tempList = new ArrayList<>();
        for (QRCodePage item : pageList) {
            tempList.add(item);
        }
        Collections.sort(tempList);
        StringBuilder mBuilder = new StringBuilder();
        for (QRCodePage qrCodePage : tempList) {
            mBuilder.append(qrCodePage.getContent());
        }
        return mBuilder.toString();
    }

    public static boolean scanIsDone(List<QRCodePage> pageList) {
        if (pageList == null || pageList.size() == 0) return false;
        boolean ret;
        int pageCount = 0;
        Set set = new HashSet<>();
        for (QRCodePage item : pageList) {
            set.add(item.getPageIndex());
            pageCount = item.getPageCount();
        }
        if (set.size() == pageCount) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    private static int getNumOfQrCodeString(int length) {
        int quality = QRQuality.Normal.getQuality();
        return (length + quality - 1) / quality;
    }

    public enum QRQuality {

        Normal(320), LOW(210);
        private int quality;

        private QRQuality(int quality) {
            this.quality = quality;
        }

        private int getQuality() {
            return this.quality;
        }

    }

    public static QRType getQRType(String text) {
        if (QRCodePage.isQrCodePage(text)) {
            return QRType.TYPE_QRCODE_PAGE;
        } else if (text.startsWith(AppUpgradeActivity.QR_BLUETOOTH)) {
            return QRType.TYPE_BLUETOOTH;
        } else {
            return QRType.TYPE_NONE;
        }
    }

    public enum QRType {
        TYPE_QRCODE_PAGE,
        TYPE_BLUETOOTH,
        TYPE_NONE
    }

}
