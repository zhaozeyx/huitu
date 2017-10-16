package com.demon.huitu.ui.basic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation

import java.util.LinkedList
import java.util.Queue

open class BaseFragment : Fragment() {
    private var mXmActivity: BaseFragmentActivity? = null

    private var mBelowFragment: BaseFragment? = null
    private var mFragmentContainer: View? = null

    private val mEnterAnimListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            notifyEnterAnimStart()
        }

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            notifyEnterAnimEnd()
        }
    }

    /**
     * 获取私有handler<BR></BR>

     * @return handler
     */
    protected val handler = Handler(Looper.getMainLooper(), Handler.Callback { msg -> this@BaseFragment.handleMessage(msg) })

    private var mIsEnterAnimRunning = false

    private val mDoAfterEnterAnim = LinkedList<Runnable>()

    // 是否锁定的标志
    /**
     * 判读该fragment是否处于锁定状态<BR></BR>

     * @return 是否处于锁定状态
     */
    protected var isUiLocked = false
        private set

    // 解除锁定的Runnable
    private val mUnlockRunnable = Runnable { isUiLocked = false }

    /**
     * 将fragment压入堆栈<BR></BR>

     * @param fragment fragment
     */
    protected fun push(fragment: BaseFragment) {
        mXmActivity!!.push(fragment)
    }

    /**
     * 将栈顶的fragment出栈<BR></BR>
     */
    fun pop() {
        mXmActivity!!.pop()
    }

    protected fun <T : BaseFragment> popToExistedInstance(fragmentClass: Class<T>): Boolean {
        return mXmActivity!!.popToExistedInstance(fragmentClass)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is BaseFragmentActivity) {
            mXmActivity = activity as BaseFragmentActivity?
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBelowFragment = belowFragment
        mFragmentContainer = view
        mFragmentContainer!!.visibility = if (savedInstanceState == null || savedInstanceState.getBoolean(KEY_CONTAINER_VIEW_IS_VISIBLE,
                true))
            View.VISIBLE
        else
            View.INVISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (mFragmentContainer != null) {
            outState!!.putBoolean(KEY_CONTAINER_VIEW_IS_VISIBLE,
                    mFragmentContainer!!.visibility == View.VISIBLE)
        }
    }

    val belowFragment: BaseFragment?
        get() = if (mXmActivity != null) mXmActivity!!.getFragmentBelow(this) else null

    fun hideBelowFragmentView() {
        val belowFragment = mBelowFragment
        if (belowFragment != null) {
            val fragmentView = belowFragment.mFragmentContainer
            if (fragmentView != null) {
                fragmentView.visibility = View.INVISIBLE
            }
        }
    }

    fun showBelowFragmentView() {
        val belowFragment = mBelowFragment
        if (belowFragment != null) {
            val fragmentView = belowFragment.mFragmentContainer
            if (fragmentView != null) {
                fragmentView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val anim: Animation?
        if (enter) {
            anim = onCreateEnterAnimation()
            anim?.setAnimationListener(mEnterAnimListener)
        } else {
            anim = onCreateExitAnimation()
            if (anim != null) {
                notifyExitAnimStart()
            }
        }
        return anim
    }

    protected fun onCreateEnterAnimation(): Animation? {
        return null
    }

    protected fun onCreateExitAnimation(): Animation? {
        return null
    }

    /**
     * 在enter过场动画播放完毕后执行<BR></BR>

     * @param runnable 执行的Runnable
     */
    protected fun doAfterEnterAnim(runnable: Runnable) {
        if (!mIsEnterAnimRunning) {
            runnable.run()
        } else {
            mDoAfterEnterAnim.offer(runnable)
        }
    }

    protected fun notifyEnterAnimStart() {
        mIsEnterAnimRunning = true
    }

    protected fun notifyEnterAnimEnd() {
        mIsEnterAnimRunning = false
        hideBelowFragmentView()
        while (!mDoAfterEnterAnim.isEmpty()) {
            mDoAfterEnterAnim.poll().run()
        }
    }

    protected fun notifyExitAnimStart() {
        showBelowFragmentView()
    }

    /**
     * 锁定该fragment<BR></BR>

     * @param duration 锁定时长
     */
    @JvmOverloads protected fun lockUi(duration: Long = 500) {
        isUiLocked = true
        handler.postDelayed(mUnlockRunnable, duration)
    }

    protected fun handleMessage(msg: Message): Boolean {
        return false
    }

    /**
     * back键被按下时的回调<BR></BR>

     * @return 是否消费该事件, false 不消费,事件将交给activity处理; true 消费该事件, activity不会处理.
     */
    fun onBackPressed(): Boolean {
        return false
    }

    override fun getLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        if (themeRes >= 0x01000000) {
            return ContextThemeWrapper(activity,
                    themeRes).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        return super.getLayoutInflater(savedInstanceState)
    }

    val themeRes: Int
        get() = -1

    companion object {
        // 过场动画播放时长
        val ANIMATION_DURATION = 400

        val KEY_CONTAINER_VIEW_IS_VISIBLE = "ContainerViewIsVisible"
    }
}
/**
 * 锁定该fragment 500ms<BR></BR>
 */