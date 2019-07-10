package com.bankledger.safegem.net.model.response

import com.google.gson.JsonObject

/**
 * EOS注册账户
 */
data class RegisterEosAccountResponse(val newAccount:String,val txId:String)

data class CheckEosAccountResponse(val account:String,val activePubKey:String,val ownerPubKey:String)

data class EOSBanlance(val balance:String,val tokenName:String)

data class GetEosSignResponse(val chainId:String,val exp:Long,val headBlockTime:Long,val lastIrreversibleBlockNum:Long,val refBlockPrefix:Long)

data class EosTxResponse(val actions:List<JsonObject>,val lastIrreversibleBlock:String)

data class UsdtBanlanceResponse(val balance:String, val reserved:String, val frozen: String)