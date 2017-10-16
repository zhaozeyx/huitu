package com.demon.huitu.injection

/*
 * 文件名: ServiceComponent
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */


import com.demon.huitu.net.service.AppService
import com.demon.huitu.net.service.UpgradeService
import com.demon.huitu.net.service.WxService
import com.demon.huitu.ui.task.TaskListFragment
import com.demon.huitu.ui.taskmap.ReportCommitActivity
import com.demon.huitu.util.UpgradeHelper

import dagger.Component

@Component(modules = arrayOf(ServiceModule::class))
interface ServiceComponent {
    fun appService(): AppService

    fun upgradeService(): UpgradeService

    fun wsService(): WxService

    fun inject(upgradeHelper: UpgradeHelper)

    fun inject(taskListFragment: TaskListFragment)

    fun inject(reportCommitActivity: ReportCommitActivity)
}
