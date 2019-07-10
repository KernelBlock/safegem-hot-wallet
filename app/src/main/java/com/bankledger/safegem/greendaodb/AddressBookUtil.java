package com.bankledger.safegem.greendaodb;

import android.content.Context;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.entity.AddressBookTb;

import java.util.List;

/**
 * Date：2018/9/6
 * Author: bankledger
 */
public class AddressBookUtil {

    private DaoManager mManager;

    public AddressBookUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 添加多个监控地址
     *
     * @return
     */
    public boolean insertAddressBook(List<AddressBookTb> addressBookTbs) {
        boolean flag = false;
        try {
            flag = mManager.getDaoSession().insertOrReplace(addressBookTbs) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 添加地址
     *
     * @return
     */
    public boolean insertAddressBook(AddressBookTb addressBookTb) {
        boolean flag = false;
        try {
            flag = mManager.getDaoSession().getAddressBookTbDao().insert(addressBookTb) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<AddressBookTb> queryAllAddressBook() {
        String sql = "where user_id = ? ORDER BY coin ASC,addr_id ASC";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId()};
        return mManager.getDaoSession().queryRaw(AddressBookTb.class, sql, condition);
    }

    /**
     * 删除单条记录
     */
    public boolean deleteAddressBook(AddressBookTb addressBookTb) {
        boolean flag = false;
        try {
            mManager.getDaoSession().delete(addressBookTb);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @return
     */
    public boolean updateAddressBook(AddressBookTb addressBookTb) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(addressBookTb);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
