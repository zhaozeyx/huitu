package com.demon.huitu.ui.basic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.demon.huitu.BootActivity
import com.demon.huitu.CustomApp
import com.demon.huitu.injection.ActivityComponent
import com.demon.huitu.injection.ActivityModule
import com.demon.huitu.injection.DaggerActivityComponent
import com.demon.huitu.net.RpcCallManager
import com.demon.huitu.net.RpcHttpError
import com.demon.huitu.ui.basic.event.LoginEvent
import com.demon.huitu.ui.basic.event.LogoutEvent
import com.demon.huitu.ui.basic.progressdialog.IProgressDialog
import com.squareup.otto.Subscribe

import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber

/**
 * Created by zhaozeyang on 16/4/11.
 */
open class BasicActivity : BaseFragmentActivity(), RpcCallManager {
    private val rpcCallManager = RpcCallManager.RpcCallManagerImpl()

    protected lateinit var component: ActivityComponent;

    private var mIsPaused = false

    private val mSessionHandler = SessionEventsHandler()

    private var mProgressDialog: IProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createActivityComponent()
        mProgressDialog = component.progressDialog()
        component.globalBus().register(this)
        component.globalBus().register(mSessionHandler)
    }

    override fun <T> manageRpcCall(observable: Flowable<T>, subscribe: DisposableSubscriber<T>) {
        rpcCallManager.manageRpcCall(observable, subscribe)
    }

    override fun onDestroy() {
        super.onDestroy()
        component.globalBus().unregister(this)
        component.globalBus().unregister(mSessionHandler)
        rpcCallManager.unSubscribeAll()
        closeProgressDialog()
    }

    override fun onPause() {
        super.onPause()
        mIsPaused = true
    }

    override fun onResume() {
        super.onResume()
        mIsPaused = false
    }

    protected fun showShortToast(msgId: Int) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show()
    }

    protected fun showShortToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示进度框<BR></BR>

     * @param message 对话框显示信息
     * *
     * @param cancelable 对话框可取消标志
     */
    protected fun showProgressDialog(message: String, cancelable: Boolean) {
        if (mIsPaused) {
            return
        }
        mProgressDialog!!.show(message, cancelable)
    }

    protected fun showProgressDialog(message: Int, cancelable: Boolean) {
        if (mIsPaused) {
            return
        }
        mProgressDialog!!.show(message, cancelable)
    }

    /**
     * 关闭进度框<BR></BR>
     */
    protected fun closeProgressDialog() {
        mProgressDialog!!.cancel()
    }

    private fun createActivityComponent() {
        component = DaggerActivityComponent.builder().activityModule(ActivityModule(this))
                .globalComponent((application as CustomApp).globalComponent)
                .build()
    }

    //
    //protected boolean isLogin() {
    //  return mActivityComponent.isLogin();
    //}

    /**
     * 当焦点停留在view上时，隐藏输入法栏

     * @param view view
     */
    protected fun hideInputWindow(view: View?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (null != imm && null != view) {
            imm.hideSoftInputFromWindow(view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 当焦点停留在view上时，显示输入法栏

     * @param view view
     */
    protected fun showInputWindow(view: View?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (null != imm && null != view) {
            imm.showSoftInput(view, 0)
        }
    }

    protected fun hasLogin(): Boolean {
        return component!!.loginSession().hasLogin()
    }

    protected fun onLogin() {

    }

    protected fun onLogout() {

    }

    protected fun showHttpError(error: RpcHttpError) {
        component.httpErrorUiNotifier().notifyUi(error)
    }

    private fun performLogout() {
        if (!mIsPaused) {
            startActivity(Intent(this, BootActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }


    private inner class SessionEventsHandler {
        @Subscribe
        fun dispatchLogin(event: LoginEvent) {
            onLogin()
        }

        @Subscribe
        fun dispatchLogout(event: LogoutEvent) {
            performLogout()
            onLogout()
        }
    }

}
