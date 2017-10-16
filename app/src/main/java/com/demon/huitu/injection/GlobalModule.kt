package com.demon.huitu.injection

/*
 * 文件名: GlobalModule
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/4/28
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.content.Context
import android.preference.PreferenceManager

import com.demon.huitu.CustomApp
import com.demon.huitu.application.BaseApplication
import com.demon.huitu.net.HttpErrorUiNotifier
import com.demon.huitu.net.SessionNotifier
import com.demon.huitu.session.LoginSession
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.squareup.otto.Bus

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class GlobalModule @Singleton
constructor(private val mApplication: BaseApplication) {

    @Singleton
    @Provides
    fun providesApplicationContext(): Context {
        return mApplication
    }

    @Singleton
    @Provides
    @GlobalBus
    fun providesGlobalBus(): Bus {
        return Bus()
    }

    @Singleton
    @Provides
    fun providesPreferences(): RxSharedPreferences {
        return RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(mApplication))
    }

    @Provides
    @Singleton
    fun providesLoginSession(): LoginSession {
        return LoginSession(mApplication)
    }

}
