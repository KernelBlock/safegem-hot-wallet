package com.bankledger.safegem.net.model.request

/**
 * 解绑冷钱包
 */
data class RemoveColdWalletRequest(var coldUniqueId: String)

/**
 * EOS注册账户
 */
data class RegisterEosAccountRequest(val newAccount: String, val activePubKey: String, val ownerPubKey: String, val coldUniqueId: String, val bluetooth: String)

/**
 * EOS账户是否正确
 */
data class CheckEosAccountRequest(val account: String)

/**
 * EOS账户信息
 */
data class EosAccountRequest(val account: String)

/**
 * EOS余额
 */
data class GetEosBalanceRequest(val account: String)

/**
 * EOS交易记录
 */
data class EosTxRequest(val account: String, val pos: String, val offset: String)

/**
 * Usdt余额
 */
data class GetUsdtBalanceRequest(val coin: String, val address: String)
