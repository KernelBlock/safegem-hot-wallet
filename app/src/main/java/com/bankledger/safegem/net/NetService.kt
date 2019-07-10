package com.bankledger.safegem.net

import com.bankledger.safegem.greendaodb.entity.UserTb
import com.bankledger.safegem.net.model.request.*
import com.bankledger.safegem.net.model.response.*
import io.reactivex.Observable
import io.reactivex.annotations.NonNull
import okhttp3.ResponseBody
import retrofit2.http.*

interface NetService {

    @Streaming
    @GET
    fun downLoadFile(@NonNull @Url url: String): Observable<ResponseBody>

    //获取图片验证码
    @POST("getImgCode")
    fun getImgCode(@Body imgCodeRequest: ImgCodeRequest): Observable<BaseResponse<String>>

    //手机号码是否已注册
    @POST("checkPhone")
    fun checkPhone(@Body phoneRequest: PhoneRequest): Observable<BaseResponse<String>>

    //短信国家代码
    @POST("getCountryCode")
    fun getCountryCode(): Observable<BaseResponse<List<CountryCodeResponse>>>

    //发送验证码
    @POST("sendSmsCode")
    fun sendSmsCode(@Body smsRequest: SmsRequest): Observable<BaseResponse<String>>

    //校验短信验证码
    @POST("validateSmsCode")
    fun validateSmsCode(@Body validateSmsCodeRequest: ValidateSmsCodeRequest): Observable<BaseResponse<ValidateSmsResponse>>

    //用户注册
    @POST("register")
    fun register(@Body registeRequest: RegisteRequest): Observable<BaseResponse<UserTb>>

    //用户登录
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Observable<BaseResponse<UserTb>>

    //修改昵称
    @POST("modifyNickName")
    fun modifyNickName(@Body updateNickNameRequest: UpdateNickNameRequest): Observable<BaseResponse<String>>

    //修改头像
    @POST("modifyPhoto")
    fun modifyPhoto(@Body modifyPhotoRequest: ModifyPhotoRequest): Observable<BaseResponse<ModifyPhotoResponse>>

    //修改头像
    @POST("modifyPassword")
    fun modifyPassword(@Body registeRequest: RegisteRequest): Observable<BaseResponse<ModifyPasswordResponse>>

    //添加冷钱包
    @POST("addColdWallet")
    fun addColdWallet(@Body addColdWalletRequest: AddColdWalletRequest): Observable<BaseResponse<AddColdWalletResponse>>

    //解绑冷钱包
    @POST("removeColdWallet")
    fun removeColdWallet(@Body removeColdWalletRequest: RemoveColdWalletRequest): Observable<BaseResponse<String>>

    //获取冷钱包
    @POST("getColdWalletList")
    fun getColdWalletList(): Observable<BaseResponse<List<AddColdWalletResponse>>>

    //添加监控地址
    @POST("addColdMonitorAddress")
    fun addColdMonitorAddress(@Body addColdMonitorAddressRequest: AddColdMonitorAddressRequest): Observable<BaseResponse<AddColdMonitorAddressResponse>>

    //获取监控地址
    @POST("getColdMonitorAddress")
    fun getColdMonitorAddress(@Body getColdMonitorAddressRequest: GetColdMonitorAddressRequest): Observable<BaseResponse<List<GetColdMonitorAddressResponse>>>

    @POST("getBestFee")
    fun getBestFee(): Observable<BaseResponse<List<BestFeeResponse>>>

    //获取交易记录
    @POST("getTransactionList")
    fun getTransactionList(@Body transactionListRequest: TransactionListRequest): Observable<BaseResponse<List<TransactionResponse>>>

    //发送btc交易
    @POST("sendTransaction")
    fun sendTransaction(@Body sendTransactionRequest: SendTransactionRequest): Observable<BaseResponse<BtcSendTransactionResponse>>

    //发送eth交易
    @POST("sendEthTransaction")
    fun sendEthTransaction(@Body sendTransactionRequest: SendTransactionRequest.SendETHTransactionRequest): Observable<BaseResponse<EthSendTransationResponse>>

    //发送etc交易
    @POST("sendEtcTransaction")
    fun sendEtcTransaction(@Body sendTransactionRequest: SendTransactionRequest.SendETHTransactionRequest): Observable<BaseResponse<EthSendTransationResponse>>

    //发送eos交易
    @POST("sendEosTransaction")
    fun sendEosTransaction(@Body sendTransactionRequest: SendTransactionRequest.SendETHTransactionRequest): Observable<BaseResponse<String>>

    //添加地址薄
    @POST("addAddressbook")
    fun addAddressbook(@Body addAddressBookRequest: AddAddressBookRequest): Observable<BaseResponse<AddColdMonitorAddressResponse>>

    //获取地址簿
    @POST("getAddressBook")
    fun getAddressBook(): Observable<BaseResponse<List<AddressBookResponse>>>

    //冷热端版本更新
    @POST("getAppVersion")
    fun getAppVersion(@Body getAppVersionRequest: GetAppVersionRequest): Observable<BaseResponse<GetAppVersionResponse>>

    //删除地址簿
    @POST("deleteAddressBook")
    fun deleteAddressBook(@Body deleteAddressBookRequest: DeleteAddressBookRequest): Observable<BaseResponse<String>>

    //删除地址簿
    @POST("updateAddressBookName")
    fun updateAddressBookName(@Body updateAddressBookNameRequest: UpdateAddressBookNameRequest): Observable<BaseResponse<String>>

    //获取消息列表
    @POST("getMsgList")
    fun getMsgList(): Observable<BaseResponse<List<GetMsgListResponse>>>

    //获取ERC20列表
    @POST("getERC20List")
    fun getERC20List(@Body getERCListRequest: GetERCListRequest): Observable<BaseResponse<List<ERCResponse>>>

    //激活ERC20列
    @POST("activeERC20")
    fun activeERC20(@Body activeERC20Request: ActiveERC20Request): Observable<BaseResponse<ERCResponse.ERC>>

    //获取ETH余额同步
    @POST("getEthAddressInfo")
    fun getEthAddressInfo(@Body activeERC20Request: GetEthAddressInfoRequest): Observable<BaseResponse<GetEthAddressResponse>>

    //获取ETC余额同步
    @POST("getEtcAddressInfo")
    fun getEtcAddressInfo(@Body activeERC20Request: GetEtcAddressInfoRequest): Observable<BaseResponse<GetEthAddressResponse>>

    //获取Eos余额同步
    @POST("getEosBalance")
    fun getEosBalance(@Body getEosBalanceRequest: GetEosBalanceRequest): Observable<BaseResponse<List<EOSBanlance>>>

    // EOS注册账户
    @POST("registerEosAccount")
    fun registerEosAccount(@Body activeERC20Request: RegisterEosAccountRequest): Observable<BaseResponse<RegisterEosAccountResponse>>

    // 获取EOS账户
    @POST("checkEosAccount")
    fun checkEosAccount(@Body checkEosAccountRequest: CheckEosAccountRequest): Observable<BaseResponse<CheckEosAccountResponse>>

    // 获取EOS账户
    @POST("getEosAccount")
    fun getEosAccount(@Body eosAccountRequest: EosAccountRequest): Observable<BaseResponse<EosAccountResponse>>

    // EOS获取签名参数
    @POST("getEosSignParam")
    fun getEosSignParam(): Observable<BaseResponse<GetEosSignResponse>>

    // EOS交易记录
    @POST("getEosActions")
    fun getEosTx(@Body eosTxRequest: EosTxRequest): Observable<BaseResponse<EosTxResponse>>

    //获取Usdt余额同步
    @POST("getBitcoinAssetBalance")
    fun getUsdtBalance(@Body getUsdtBalanceRequest: GetUsdtBalanceRequest): Observable<BaseResponse<UsdtBanlanceResponse>>

    //获取Usdt交易记录
    @POST("getBitcoinAssetTransactionList")
    fun getUsdtTx(@Body transactionListRequest: TransactionListRequest): Observable<BaseResponse<List<UsdtTransactionResponse>>>

    //获取SAFE资产列表
    @POST("getAssetInfoList")
    fun getAssetInfoList(@Body transactionListRequest: GetAssetIdRequest): Observable<BaseResponse<List<GetAssetIdResponse>>>

}