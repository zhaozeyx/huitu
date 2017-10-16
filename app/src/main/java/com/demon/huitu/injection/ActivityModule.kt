package com.demon.huitu.injection

/*
 * 文件名: ActivityModule
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.app.Activity

import com.demon.huitu.net.RpcCallManager
import com.demon.huitu.ui.basic.progressdialog.IProgressDialog
import com.demon.huitu.ui.basic.progressdialog.ProgressDialogImp
import com.demon.huitu.util.UpgradeHelper
import com.squareup.otto.Bus

import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val mActivity: Activity) {

    @Provides
    fun providesActivity(): Activity {
        return mActivity
    }

    @Provides
    @ActivityBus
    fun providesActivityBus(): Bus {
        return Bus()
    }

    @Provides
    fun providesRpcCallManager(): RpcCallManager {
        return RpcCallManager.RpcCallManagerImpl()
    }

    @Provides
    fun providesProgressDialog(): IProgressDialog {
        return ProgressDialogImp(mActivity)
    }

    @Provides
    fun providesUpgradeHelper(): UpgradeHelper {
        return UpgradeHelper(mActivity)
    }
}
