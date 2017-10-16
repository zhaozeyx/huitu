package com.demon.huitu

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.demon.huitu.application.BaseApplication
import com.demon.huitu.log.Logger

/*
 * 文件名: CustomApp
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/4/28
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

class CustomApp : BaseApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Logger.init()
    }

}
