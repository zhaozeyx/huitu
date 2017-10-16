package com.demon.huitu.injection

/*
 * 文件名: ActivityComponent
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import com.demon.huitu.net.RpcCallManager
import com.demon.huitu.ui.basic.progressdialog.IProgressDialog
import com.demon.huitu.util.UpgradeHelper
import com.squareup.otto.Bus

import dagger.Component

@ActivityScope
@Component(dependencies = arrayOf(GlobalComponent::class), modules = arrayOf(ActivityModule::class))
interface ActivityComponent : GlobalComponent {

    @get:ActivityBus
    val activityBus: Bus

    fun rpcCallManager(): RpcCallManager

    fun upgradeHelper(): UpgradeHelper

    fun progressDialog(): IProgressDialog
}
