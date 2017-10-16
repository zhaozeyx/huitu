package com.demon.huitu.net.service

/*
 * 文件名: AppService
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */


import com.demon.huitu.net.model.ResponseModel
import com.demon.huitu.net.model.UpgradeModel
import com.demon.huitu.net.model.UserInfo
import com.demon.huitu.net.model.VerifyWXBindModel
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @FormUrlEncoded
    @POST("User/doLogin")
    fun login(@Field(value = "account") account: String, @Field(value = "password")
    password: String): Flowable<ResponseModel<UserInfo>>

    @GET
    @Streaming
    fun downloadAPK(@Url fileUrl: String): Flowable<Response<ResponseBody>>

    // TODO 为了让编译通过临时加的方法
    @FormUrlEncoded
    @POST("CheckVersion/check_version")
    fun checkUpgrade(@Field(value = "type") type: String, @Field(value = "version")
    version: String): Flowable<ResponseModel<UpgradeModel>>

    @FormUrlEncoded
    @GET("index.php?m=api&c=Index&a=weixinBindList")
    fun verifyWXBind(@Field(value = "wxopenid") openId: String,
                     @Field(value = "wxunionid") unionId: String): Flowable<ResponseModel<VerifyWXBindModel>>

    @FormUrlEncoded
    @POST("index.php?s=apiv2/Oauth/authorize")
    fun wxLogin(@Field(value = "wxopenid") openId: String,
                @Field(value = "wxunionid") unionId: String,
                @Field(value = "email") email: String): Single<ResponseModel<UserInfo>>

    @FormUrlEncoded
    @GET("index.php?m=api&c=Index&a=weixinBindList")
    fun verifyWXBindCallback(@Field(value = "wxopenid") openId: String,
                             @Field(value = "wxunionid") unionId: String, callback: Callback<VerifyWXBindModel>)

    @FormUrlEncoded
    @POST("index.php?s=apiv2/Oauth/authorize")
    fun wxLoginCallback(@Field(value = "wxopenid") openId: String,
                        @Field(value = "wxunionid") unionId: String,
                        @Field(value = "email") email: String, callback: Callback<ResponseModel<UserInfo>>)

}
