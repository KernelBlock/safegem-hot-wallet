package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.dao.MessageTbDao;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;
import com.bankledger.safegem.greendaodb.entity.MessageTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.message.FormatOut;
import com.bankledger.safegem.message.FormatTx;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.SafeUtils;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/11
 * Author: bankledger
 */
public class MessageUtils {

    private DaoManager mManager;

    public MessageUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public List<MessageTb> queryAllMessage() {
        String sql = "where user_id = ? ORDER BY date DESC";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId()};
        return mManager.getDaoSession().queryRaw(MessageTb.class, sql, condition);
    }

    /**
     * 获取最新msgId
     */
    public String queryLastMsgId() {
        String address = "";
        String sql = "select msg_id from " + MessageTbDao.TABLENAME + " where user_id = ? ORDER BY date DESC limit 1";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId())});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    /**
     * 添加消息
     *
     * @return
     */
    public boolean insertColdWallets(List<MessageTb> messageTbList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MessageTb messageTb : messageTbList) {
                        mManager.getDaoSession().insert(messageTb);
                    }
                }
            });
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 查询前两条消息标题
     */
    public List<MessageTb> queryFirstMessage() {
        List<MessageTb> list = new ArrayList<>();
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                String sql = "where user_id = ? " + "ORDER BY date DESC limit 2";
                String[] condition = new String[]{SafeGemApplication.getInstance().getUserId()};
                list.addAll(mManager.getDaoSession().queryRaw(MessageTb.class, sql, condition));
            }
        });
        return list;
    }

    /**
     * 添加交易信息
     */
    public boolean insertTxMessage(String coin, String coldUniqueId, List<FormatTx> txList) {
        List<MessageTb> messageList = new ArrayList<>();
        TxUtil txUtil = new TxUtil(SafeGemApplication.getInstance());
        for (FormatTx tx : txList) {
            if (tx.isSafeAsset()) {
                continue;
            }
            if (!isExitMessage(tx.txid, coldUniqueId)) {
                MessageTb messageTb = new MessageTb();
                messageTb.userId = SafeGemApplication.getInstance().getUserInfo().getUserId();
                TxTb txTb = txUtil.getTxTbByTxHash(tx.txid);
                if (txTb.getBtcAsssetType() != Constants.BTC_DEFAULT) {
                    continue;
                }
                boolean isSend = txUtil.txIsSend(txTb.getTxHash(), coldUniqueId);
                String address = txUtil.getTxAddress(txTb.getTxHash(), coldUniqueId, isSend);
                String amount = txUtil.getTxAmount(txTb.getTxHash(), coldUniqueId, isSend);
                String coinName = StringUtil.getDisplayName(txTb.getCoin());
                messageTb.txHash = tx.txid;
                messageTb.msgId = tx.txid;
                if (isSend) {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.send_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.send) + address;
                    messageTb.content = BigDecimalUtils.formatBtc(amount) + " " + coinName;
                    messageTb.msgType = Constants.SEND_MSG_TYPE;
                } else {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.receive_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.from) + address;
                    messageTb.content = BigDecimalUtils.formatBtc(amount) + " " + coinName;
                    messageTb.msgType = Constants.RECEIVE_MSG_TYPE;
                }
                messageTb.coinType = Utils.coin2Type(coin);
                messageTb.date = DateTimeUtil.getDateTimeString(txTb.getTime() * 1000);
                messageTb.coldUniqueId = coldUniqueId;
                messageList.add(messageTb);
            }
        }
        return insertMessage(messageList);
    }

    public boolean insertUsdtMessage(String coin, List<UsdtTxTb> txList) {
        List<MessageTb> messageList = new ArrayList<>();
        String address = SafeGemApplication.getInstance().getCurrentUSDTAddress();
        String coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        for (UsdtTxTb item : txList) {
            if (!isExitMessage(item.getTxid(), coldUniqueId)) {
                MessageTb messageTb = new MessageTb();
                messageTb.userId = SafeGemApplication.getInstance().getUserInfo().getUserId();
                messageTb.txHash = item.getTxid();
                messageTb.msgId = item.getTxid();
                if (item.getSendingaddress().equals(address)) {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.send_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.send) + address;
                    messageTb.content = " - " + item.getAmount() + " " + coin;
                    messageTb.msgType = Constants.SEND_MSG_TYPE;
                } else {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.receive_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.from) + address;
                    messageTb.content = " + " + item.getAmount() + " " + coin;
                    messageTb.msgType = Constants.RECEIVE_MSG_TYPE;
                }
                messageTb.coinType = Utils.coin2Type(coin);
                messageTb.date = item.getFormatDateTime();
                messageTb.coldUniqueId = coldUniqueId;
                messageList.add(messageTb);
            }
        }
        return insertMessage(messageList);
    }

    public boolean insertEosMessage(String coin, List<EosTxTb> txList) {
        List<MessageTb> messageList = new ArrayList<>();
        String account = SafeGemApplication.getInstance().getCurrentEOSAccount();
        String coldUniqueId = SafeGemApplication.getInstance().getColdUniqueId();
        for (EosTxTb item : txList) {
            if (!isExitMessage(item.getTxId(), coldUniqueId)) {
                MessageTb messageTb = new MessageTb();
                messageTb.userId = SafeGemApplication.getInstance().getUserInfo().getUserId();
                messageTb.txHash = item.getTxId();
                messageTb.msgId = item.getTxId();
                if (item.getFrom().equals(item.getAccount())) {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.send_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.send) + account;
                    messageTb.content = " - " + item.getAmount() + " " + item.getCoin();
                    messageTb.msgType = Constants.SEND_MSG_TYPE;
                } else {
                    messageTb.setTitle(SafeGemApplication.getInstance().getString(R.string.receive_broad));
                    messageTb.msgUrl = SafeGemApplication.getInstance().getString(R.string.from) + account;
                    messageTb.content = " + " + item.getAmount() + " " + item.getCoin();
                    messageTb.msgType = Constants.RECEIVE_MSG_TYPE;
                }
                messageTb.coinType = Utils.coin2Type(coin);
                messageTb.date = item.getFormatDateTime();
                messageTb.coldUniqueId = coldUniqueId;
                messageList.add(messageTb);
            }
        }
        return insertMessage(messageList);
    }

    /**
     * 插入消息
     *
     * @param messageList
     * @return
     */
    public boolean insertMessage(List<MessageTb> messageList) {
        boolean flag;
        try {
            if (messageList != null && messageList.size() > 0) {
                mManager.getDaoSession().getMessageTbDao().insertOrReplaceInTx(messageList);
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 是否是监控地址
     *
     * @return
     */
    public boolean isExitMessage(String msgId, String cold_unique_id) {
        int count = 0;
        String sql = "select count(*) from " + MessageTbDao.TABLENAME + " where user_id = ? and msg_id = ? and cold_unique_id = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), msgId, cold_unique_id};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    public boolean isExitMessage(String msgId) {
        int count = 0;
        String sql = "select count(*) from " + MessageTbDao.TABLENAME + " where user_id = ? and msg_id = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), msgId};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    public boolean deleteByColdWallet(String coldUniqueId) {
        boolean flag;
        String sql = "DELETE FROM " + MessageTbDao.TABLENAME + " where cold_unique_id = ?";
        String[] condition = new String[]{coldUniqueId};
        try {
            mManager.getDaoSession().getDatabase().execSQL(sql, condition);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

}
