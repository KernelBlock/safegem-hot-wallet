package com.bankledger.safegem.utils

import android.os.Environment
import java.util.*


class Constants {

    companion object {

        const val isDebug = false;
        const val NET_BASE_URL_DEBUG = "http://bankledger.f3322.net:8081/api/v1/" // 测试网络外网接口
        const val NET_BASE_URL_RELEASE = "https://safegemapi.anwang.com/api/v1/"  // 正式环境服务器请求

        var LANGUAGE_HEADER = "cn"  // 语言
        var AUTHORIZATION_HEADER = ""

        const val INTENT_KEY1 = "intent_key1"
        const val INTENT_KEY2 = "intent_key2"
        const val INTENT_KEY3 = "intent_key3"
        const val INTENT_KEY4 = "intent_key4"
        const val INTENT_KEY5 = "intent_key5"

        const val BTC = "BTC"
        const val LTC = "LTC"
        const val SAFE = "SAFE"
        const val DASH = "DASH"
        const val QTUM = "QTUM"
        const val BCH = "BCH"
        const val BCHABC = "BCHABC"
        const val BSV = "BSV"
        const val BCHSV = "BCHSV"
        const val BTG = "BTG"
        const val ETH = "ETH"
        const val FTO = "FTO"
        const val ETC = "ETC"
        const val USDT = "USDT"
        const val EOS = "EOS"

        const val BTC_FULL_NAME = "bitcoin"
        const val LTC_FULL_NAME = "litecoin"
        const val SAFE_FULL_NAME = "safe"
        const val DASH_FULL_NAME = "dash"
        const val QTUM_FULL_NAME = "qtum"
        const val BCH_FULL_NAME = "bitcoincash"
        const val BSV_FULL_NAME = "bitcoinsv"
        const val BTG_FULL_NAME = "bitcoingold"
        const val FTO_FULL_NAME = "futurocoin"
        const val USDT_FULL_NAME = "tether"
        const val EOS_FULL_NAME = "eos"
        const val ETH_FULL_NAME = "ethereum"
        const val ETC_FULL_NAME = "ethereumclassic"

        var COIN_ARR: Array<String> = arrayOf(BCH, BSV, BTC, BTG, DASH, EOS, ETC, ETH, FTO, LTC, QTUM, SAFE, USDT)

        const val LANGUAGE = "language_chose"

        const val REQUEST_CODE = 1 //启动activity标识

        const val RESULT_CODE = 2 //启动activity标识
        const val RESPONSE_DATA = "response" //activity返回数据标识

        const val PHONE = "phone"
        const val SMS_CODE = "code"
        const val DATABASE = "safehotwallet" // 数据库名称

        const val LEFT_BRACKET = "("
        const val RIGHT_BRACKET = ")"

        const val CURRENT_WALLET_ID = "current_wallet_id"
        const val CURRENT_WALLET_POSITION = "current_wallet_poistion"

        const val ADDWALLET = 1
        const val ADD_ADDRESS = 2
        const val ADD_ADDRESS_BOOK = 3
        const val ALL_SCAN = 4
        const val EOS_SCAN = 5
        const val MONITOR_ADDRESS = 6

        const val SCAN_FLAG = "scan_flag"
        const val SCAN_DATA = "scan_data"
        const val INTENT_DATA = "intent_data"
        const val WALLET_ID_DATA = "wallet_id_data"
        const val URL = "url"

        const val TYPE_SEND_TX = 1 //发送交易
        const val TYPE_ADDR = 2  //同步余额
        const val TYPE_MON = 3  // 添加监控
        const val TYPE_ADDWALLET = 4  // 添加冷钱包
        const val TYPE_UPDATE_WALLET = 5  //升级冷钱包
        const val TYPE_EOS = 6 //EOS传输
        const val TYPE_LANGUAGE = 7 //切换语言

        const val REST_COUNT = 20
        const val EOS_TX_COUNT = 20
        const val PAGE_SIZE = 20

        const val NEW_COIN = 1
        const val DIMISS = 2
        const val SHOW = 3
        const val SHOW_DATA = 4
        const val COLD_VERSION_MESSAGE = "cold_version_message"

        /**
         * 币系标识
         */
        const val COIN_BTC = 0
        const val COIN_ETH = 1
        const val COIN_ETH_TOKEN = 2
        const val COIN_ETC = 3
        const val COIN_EOS = 4
        const val COIN_EOS_TOKEN = 5
        const val COIN_USDT = 6
        const val COIN_SAFE_ASSET = 7

        //冷钱包apk下载路径
        val COLD_WALLET_PATH = Environment.getExternalStorageDirectory().absolutePath + "/cold"
        val COLD_WALLET_FILE_NAME = Utils.getBase64("coldwallet")

        //热钱包apk下载路径
        val HOT_WALLET_PATH = Environment.getExternalStorageDirectory().absolutePath + "/hot"
        val HOT_WALLET_FILE_NAME = Utils.getBase64("hotwallet")

        const val provider = ".provider"
        const val RECEIVE_MSG_TYPE = 2
        const val SEND_MSG_TYPE = 3

//        const val BUGLY_KEY = "60f050968f"

        const val CHINA_CODE = "86"

        const val UN_CONFIRM = 0;
        const val CONFIRMED = 1;

        const val HAS_NEW_VERSION = 1
        const val HOT_PRODUCT_TYPE = 1
        const val COLD_PRODUCT_TYPE = 2
        const val QRUERY_COMPLETE_MESSAGE = "query_transaction_complete"
        const val SYMS_CODE_TIME = 90
        const val RECEIVER = ".RECEIVER"

        const val BTC_ASSET_BEGIN = "6a";
        const val BTC_ASSET_USDT_FLAG_TEST = "6f6d6e690000000000000002";
        const val BTC_ASSET_USDT_FLAG_RELEASE = "6f6d6e69000000000000001f";

        const val BTC_DEFAULT = 0;
        const val BTC_ASSET_USDT = 1;

    }
}