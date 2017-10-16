package com.demon.huitu.net.service

import com.demon.huitu.net.model.WxTokenModel
import io.reactivex.Flowable
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Query

/*
 * 文件名: WxService
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/7/9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
open interface WxService {

    @GET("access_token")
    fun getAccessToken(@Query(value = "appid") appId: String, @Query(value = "secret")
    appSecret: String, @Query(value = "code") code: String): Flowable<WxTokenModel>

    @GET("access_token")
    fun getAccessTokenCallback(@Query(value = "appid") appId: String, @Query(value = "secret")
    appSecret: String, @Query(value = "code") code: String, callback: Callback<WxTokenModel>)
}