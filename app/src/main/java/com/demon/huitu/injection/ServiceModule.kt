package com.demon.huitu.injection

/*
 * 文件名: ServiceModule
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */


import com.demon.huitu.net.RetrofitFactory
import com.demon.huitu.net.service.AppService
import com.demon.huitu.net.service.UpgradeService
import com.demon.huitu.net.service.WxService

import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    fun providesAppService(): AppService {
        return RetrofitFactory.createAppService()
    }

    @Provides
    fun providesUpgradeService(): UpgradeService {
        return RetrofitFactory.createUpgradeService()
    }

    @Provides
    fun providesWxService(): WxService {
        return RetrofitFactory.createWxService()
    }
}
