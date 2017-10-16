package com.demon.huitu.ui.basic

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

import com.demon.huitu.CustomApp
import com.demon.huitu.injection.ActivityComponent
import com.demon.huitu.injection.ActivityModule
import com.demon.huitu.injection.DaggerActivityComponent
import com.demon.huitu.net.RpcCallManager
import com.demon.huitu.ui.basic.event.LoginEvent
import com.demon.huitu.ui.basic.event.LogoutEvent
import com.demon.huitu.ui.basic.progressdialog.IProgressDialog
import com.squareup.otto.Subscribe

import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber

abstract class BasicFragment : BaseFragment(), RpcCallManager {
    private val mClassName = javaClass.simpleName


    /**
     * 页面是否进入pause状态
     */
    protected var isPaused: Boolean = false
        private set

    lateinit  var component: ActivityComponent

    protected var rpcCallManager = RpcCallManager.RpcCallManagerImpl()

    private val mSessionEventsHandler = SessionEventsHandler()

    private var mProgressDialog: IProgressDialog? = null

    fun createComponent(activity: Activity): ActivityComponent {
        return DaggerActivityComponent.builder().activityModule(ActivityModule(activity))
                .globalComponent((activity.application as CustomApp).globalComponent)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component = createComponent(activity)
        mProgressDialog = component!!.progressDialog()
        super.onCreate(savedInstanceState)
        component.globalBus().register(this)
        component.globalBus().register(mSessionEventsHandler)
    }

    override fun onResume() {
        super.onResume()
        isPaused = false
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    /**
     * 显示进度框<BR></BR>

     * @param message 对话框显示信息
     * *
     * @param cancelable 对话框可取消标志
     */
    @JvmOverloads protected fun showProgressDialog(message: String, cancelable: Boolean = true) {
        if (isPaused) {
            return
        }
        mProgressDialog!!.show(message, cancelable)
    }

    /**
     * 显示进度框<BR></BR>

     * @param msgResId 对话框显示信息
     */
    protected fun showProgressDialog(msgResId: Int) {
        showProgressDialog(resources.getString(msgResId), true)
    }

    /**
     * 显示进度框<BR></BR>

     * @param msgResId 对话框显示信息
     * *
     * @param cancelable 是否可取消的标志
     */
    protected fun showProgressDialog(msgResId: Int, cancelable: Boolean) {
        showProgressDialog(resources.getString(msgResId), cancelable)
    }

    /**
     * 关闭进度框<BR></BR>
     */
    protected fun closeProgressDialog() {
        mProgressDialog!!.cancel()
    }


    /**
     * 显示短时间的提示内容<BR></BR>

     * @param content 提示内容
     */
    protected fun showShortToast(content: String) {
        Toast.makeText(activity, content, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示toast<BR></BR>

     * @param resId 消息资源Id
     */
    protected fun showShortToast(resId: Int) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show()
    }


    override fun <T> manageRpcCall(observable: Flowable<T>, subscribe: DisposableSubscriber<T>) {
        rpcCallManager.manageRpcCall(observable, subscribe)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rpcCallManager.unSubscribeAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeProgressDialog()
        component.globalBus().unregister(mSessionEventsHandler)
        component.globalBus().unregister(this)
    }


    protected fun onLogin() {

    }

    protected fun onLogout() {

    }

    private inner class SessionEventsHandler {
        @Subscribe
        fun dispatchLogin(event: LoginEvent) {
            onLogin()
        }

        @Subscribe
        fun dispatchLogout(event: LogoutEvent) {
            onLogout()
        }
    }
}
/**
 * 显示进度框<BR></BR>

 * @param message 对话框显示信息
 */
// 默认可取消当前对话框