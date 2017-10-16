package com.demon.huitu.application

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.demon.huitu.injection.DaggerGlobalComponent
import com.demon.huitu.injection.GlobalComponent
import com.demon.huitu.injection.GlobalModule
import com.demon.huitu.log.Logger

/*
 * 文件名: BaseApplication
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
open class BaseApplication : Application() {

    val globalComponent: GlobalComponent by lazy {
        DaggerGlobalComponent
                .builder()
                .globalModule(GlobalModule(this))
                .build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Logger.init()
    }

}