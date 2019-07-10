package com.bankledger.safegem.greendaodb;

import android.content.Context;

import com.bankledger.safegem.greendaodb.entity.UserTb;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.Utils;

import java.util.List;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class UserUtil {

    private DaoManager mManager;

    public UserUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成用户信息的插入，如果表未创建，先创建表
     *
     * @return
     */
    public boolean insertUser(UserTb userTb) {
        boolean flag;
        try {
            mManager.getDaoSession().getUserTbDao().insert(userTb);
            flag = true;
        } catch (Exception e) {
            flag = false;
        }
        if (flag) {
            Constants.Companion.setAUTHORIZATION_HEADER(Utils.getBase64(userTb.getUserId() + ":" + userTb.getTokenId()));
        }
        return flag;
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    public UserTb queryUserTb() {
        List<UserTb> userList = mManager.getDaoSession().loadAll(UserTb.class);
        if (userList.size() > 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 修改一条数据
     *
     * @return
     */
    public boolean updateUserTb(UserTb userTb) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(userTb);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


}
