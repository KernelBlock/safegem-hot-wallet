package com.bankledger.safegem.greendaodb.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.bankledger.safegem.greendaodb.entity.UserTb;
import com.bankledger.safegem.greendaodb.entity.AddressBookTb;
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb;
import com.bankledger.safegem.greendaodb.entity.MessageTb;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.EosRamTb;
import com.bankledger.safegem.greendaodb.entity.EosDelegateTb;
import com.bankledger.safegem.greendaodb.entity.TxOutsTb;
import com.bankledger.safegem.greendaodb.entity.EthTokenTb;
import com.bankledger.safegem.greendaodb.entity.TxInsTb;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.EosTxTb;

import com.bankledger.safegem.greendaodb.dao.UserTbDao;
import com.bankledger.safegem.greendaodb.dao.AddressBookTbDao;
import com.bankledger.safegem.greendaodb.dao.UsdtTxTbDao;
import com.bankledger.safegem.greendaodb.dao.MessageTbDao;
import com.bankledger.safegem.greendaodb.dao.EosAccountTbDao;
import com.bankledger.safegem.greendaodb.dao.EosBalanceTbDao;
import com.bankledger.safegem.greendaodb.dao.EosRamTbDao;
import com.bankledger.safegem.greendaodb.dao.EosDelegateTbDao;
import com.bankledger.safegem.greendaodb.dao.TxOutsTbDao;
import com.bankledger.safegem.greendaodb.dao.EthTokenTbDao;
import com.bankledger.safegem.greendaodb.dao.TxInsTbDao;
import com.bankledger.safegem.greendaodb.dao.ColdWalletTbDao;
import com.bankledger.safegem.greendaodb.dao.MonitorAddressTbDao;
import com.bankledger.safegem.greendaodb.dao.TxTbDao;
import com.bankledger.safegem.greendaodb.dao.SafeAssetTbDao;
import com.bankledger.safegem.greendaodb.dao.EosTxTbDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userTbDaoConfig;
    private final DaoConfig addressBookTbDaoConfig;
    private final DaoConfig usdtTxTbDaoConfig;
    private final DaoConfig messageTbDaoConfig;
    private final DaoConfig eosAccountTbDaoConfig;
    private final DaoConfig eosBalanceTbDaoConfig;
    private final DaoConfig eosRamTbDaoConfig;
    private final DaoConfig eosDelegateTbDaoConfig;
    private final DaoConfig txOutsTbDaoConfig;
    private final DaoConfig ethTokenTbDaoConfig;
    private final DaoConfig txInsTbDaoConfig;
    private final DaoConfig coldWalletTbDaoConfig;
    private final DaoConfig monitorAddressTbDaoConfig;
    private final DaoConfig txTbDaoConfig;
    private final DaoConfig safeAssetTbDaoConfig;
    private final DaoConfig eosTxTbDaoConfig;

    private final UserTbDao userTbDao;
    private final AddressBookTbDao addressBookTbDao;
    private final UsdtTxTbDao usdtTxTbDao;
    private final MessageTbDao messageTbDao;
    private final EosAccountTbDao eosAccountTbDao;
    private final EosBalanceTbDao eosBalanceTbDao;
    private final EosRamTbDao eosRamTbDao;
    private final EosDelegateTbDao eosDelegateTbDao;
    private final TxOutsTbDao txOutsTbDao;
    private final EthTokenTbDao ethTokenTbDao;
    private final TxInsTbDao txInsTbDao;
    private final ColdWalletTbDao coldWalletTbDao;
    private final MonitorAddressTbDao monitorAddressTbDao;
    private final TxTbDao txTbDao;
    private final SafeAssetTbDao safeAssetTbDao;
    private final EosTxTbDao eosTxTbDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userTbDaoConfig = daoConfigMap.get(UserTbDao.class).clone();
        userTbDaoConfig.initIdentityScope(type);

        addressBookTbDaoConfig = daoConfigMap.get(AddressBookTbDao.class).clone();
        addressBookTbDaoConfig.initIdentityScope(type);

        usdtTxTbDaoConfig = daoConfigMap.get(UsdtTxTbDao.class).clone();
        usdtTxTbDaoConfig.initIdentityScope(type);

        messageTbDaoConfig = daoConfigMap.get(MessageTbDao.class).clone();
        messageTbDaoConfig.initIdentityScope(type);

        eosAccountTbDaoConfig = daoConfigMap.get(EosAccountTbDao.class).clone();
        eosAccountTbDaoConfig.initIdentityScope(type);

        eosBalanceTbDaoConfig = daoConfigMap.get(EosBalanceTbDao.class).clone();
        eosBalanceTbDaoConfig.initIdentityScope(type);

        eosRamTbDaoConfig = daoConfigMap.get(EosRamTbDao.class).clone();
        eosRamTbDaoConfig.initIdentityScope(type);

        eosDelegateTbDaoConfig = daoConfigMap.get(EosDelegateTbDao.class).clone();
        eosDelegateTbDaoConfig.initIdentityScope(type);

        txOutsTbDaoConfig = daoConfigMap.get(TxOutsTbDao.class).clone();
        txOutsTbDaoConfig.initIdentityScope(type);

        ethTokenTbDaoConfig = daoConfigMap.get(EthTokenTbDao.class).clone();
        ethTokenTbDaoConfig.initIdentityScope(type);

        txInsTbDaoConfig = daoConfigMap.get(TxInsTbDao.class).clone();
        txInsTbDaoConfig.initIdentityScope(type);

        coldWalletTbDaoConfig = daoConfigMap.get(ColdWalletTbDao.class).clone();
        coldWalletTbDaoConfig.initIdentityScope(type);

        monitorAddressTbDaoConfig = daoConfigMap.get(MonitorAddressTbDao.class).clone();
        monitorAddressTbDaoConfig.initIdentityScope(type);

        txTbDaoConfig = daoConfigMap.get(TxTbDao.class).clone();
        txTbDaoConfig.initIdentityScope(type);

        safeAssetTbDaoConfig = daoConfigMap.get(SafeAssetTbDao.class).clone();
        safeAssetTbDaoConfig.initIdentityScope(type);

        eosTxTbDaoConfig = daoConfigMap.get(EosTxTbDao.class).clone();
        eosTxTbDaoConfig.initIdentityScope(type);

        userTbDao = new UserTbDao(userTbDaoConfig, this);
        addressBookTbDao = new AddressBookTbDao(addressBookTbDaoConfig, this);
        usdtTxTbDao = new UsdtTxTbDao(usdtTxTbDaoConfig, this);
        messageTbDao = new MessageTbDao(messageTbDaoConfig, this);
        eosAccountTbDao = new EosAccountTbDao(eosAccountTbDaoConfig, this);
        eosBalanceTbDao = new EosBalanceTbDao(eosBalanceTbDaoConfig, this);
        eosRamTbDao = new EosRamTbDao(eosRamTbDaoConfig, this);
        eosDelegateTbDao = new EosDelegateTbDao(eosDelegateTbDaoConfig, this);
        txOutsTbDao = new TxOutsTbDao(txOutsTbDaoConfig, this);
        ethTokenTbDao = new EthTokenTbDao(ethTokenTbDaoConfig, this);
        txInsTbDao = new TxInsTbDao(txInsTbDaoConfig, this);
        coldWalletTbDao = new ColdWalletTbDao(coldWalletTbDaoConfig, this);
        monitorAddressTbDao = new MonitorAddressTbDao(monitorAddressTbDaoConfig, this);
        txTbDao = new TxTbDao(txTbDaoConfig, this);
        safeAssetTbDao = new SafeAssetTbDao(safeAssetTbDaoConfig, this);
        eosTxTbDao = new EosTxTbDao(eosTxTbDaoConfig, this);

        registerDao(UserTb.class, userTbDao);
        registerDao(AddressBookTb.class, addressBookTbDao);
        registerDao(UsdtTxTb.class, usdtTxTbDao);
        registerDao(MessageTb.class, messageTbDao);
        registerDao(EosAccountTb.class, eosAccountTbDao);
        registerDao(EosBalanceTb.class, eosBalanceTbDao);
        registerDao(EosRamTb.class, eosRamTbDao);
        registerDao(EosDelegateTb.class, eosDelegateTbDao);
        registerDao(TxOutsTb.class, txOutsTbDao);
        registerDao(EthTokenTb.class, ethTokenTbDao);
        registerDao(TxInsTb.class, txInsTbDao);
        registerDao(ColdWalletTb.class, coldWalletTbDao);
        registerDao(MonitorAddressTb.class, monitorAddressTbDao);
        registerDao(TxTb.class, txTbDao);
        registerDao(SafeAssetTb.class, safeAssetTbDao);
        registerDao(EosTxTb.class, eosTxTbDao);
    }
    
    public void clear() {
        userTbDaoConfig.clearIdentityScope();
        addressBookTbDaoConfig.clearIdentityScope();
        usdtTxTbDaoConfig.clearIdentityScope();
        messageTbDaoConfig.clearIdentityScope();
        eosAccountTbDaoConfig.clearIdentityScope();
        eosBalanceTbDaoConfig.clearIdentityScope();
        eosRamTbDaoConfig.clearIdentityScope();
        eosDelegateTbDaoConfig.clearIdentityScope();
        txOutsTbDaoConfig.clearIdentityScope();
        ethTokenTbDaoConfig.clearIdentityScope();
        txInsTbDaoConfig.clearIdentityScope();
        coldWalletTbDaoConfig.clearIdentityScope();
        monitorAddressTbDaoConfig.clearIdentityScope();
        txTbDaoConfig.clearIdentityScope();
        safeAssetTbDaoConfig.clearIdentityScope();
        eosTxTbDaoConfig.clearIdentityScope();
    }

    public UserTbDao getUserTbDao() {
        return userTbDao;
    }

    public AddressBookTbDao getAddressBookTbDao() {
        return addressBookTbDao;
    }

    public UsdtTxTbDao getUsdtTxTbDao() {
        return usdtTxTbDao;
    }

    public MessageTbDao getMessageTbDao() {
        return messageTbDao;
    }

    public EosAccountTbDao getEosAccountTbDao() {
        return eosAccountTbDao;
    }

    public EosBalanceTbDao getEosBalanceTbDao() {
        return eosBalanceTbDao;
    }

    public EosRamTbDao getEosRamTbDao() {
        return eosRamTbDao;
    }

    public EosDelegateTbDao getEosDelegateTbDao() {
        return eosDelegateTbDao;
    }

    public TxOutsTbDao getTxOutsTbDao() {
        return txOutsTbDao;
    }

    public EthTokenTbDao getEthTokenTbDao() {
        return ethTokenTbDao;
    }

    public TxInsTbDao getTxInsTbDao() {
        return txInsTbDao;
    }

    public ColdWalletTbDao getColdWalletTbDao() {
        return coldWalletTbDao;
    }

    public MonitorAddressTbDao getMonitorAddressTbDao() {
        return monitorAddressTbDao;
    }

    public TxTbDao getTxTbDao() {
        return txTbDao;
    }

    public SafeAssetTbDao getSafeAssetTbDao() {
        return safeAssetTbDao;
    }

    public EosTxTbDao getEosTxTbDao() {
        return eosTxTbDao;
    }

}
