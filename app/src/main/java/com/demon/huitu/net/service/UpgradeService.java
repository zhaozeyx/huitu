package com.demon.huitu.net.service;
/*
 * 文件名: UpgradeService
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */


import com.demon.huitu.net.model.ResponseModel;
import com.demon.huitu.net.model.UpgradeModel;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpgradeService {
    @FormUrlEncoded
    @POST("check/update")
    Flowable<ResponseModel<UpgradeModel>> checkUpgrade(@Field(value = "appid") String appId, @Field
            (value = "version") String version
            , @Field(value = "imei") String imei);
}
