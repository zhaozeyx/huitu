package com.demon.huitu.injection

/*
 * 文件名: GlobalComponent
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.content.Context
import com.demon.huitu.CustomApp

import com.demon.huitu.net.HttpErrorUiNotifier
import com.demon.huitu.net.SessionNotifier
import com.demon.huitu.session.LoginSession
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.squareup.otto.Bus

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = arrayOf(GlobalModule::class))
interface GlobalComponent {
    fun applicationContext(): Context

    @GlobalBus
    fun globalBus(): Bus

    fun appPreferences(): RxSharedPreferences

    fun loginSession(): LoginSession

    fun httpErrorUiNotifier(): HttpErrorUiNotifier

    fun sessionNotifier(): SessionNotifier
}
