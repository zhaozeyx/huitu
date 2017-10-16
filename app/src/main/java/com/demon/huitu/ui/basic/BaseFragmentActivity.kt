package com.demon.huitu.ui.basic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import java.util.*

/*
 * 文件名: BaseFragmentActivity
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/12
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

open class BaseFragmentActivity : AppCompatActivity() {
    private var mContainerId: Int = 0
    private val mFragments = ArrayList<BaseFragment>()
    private var mFragmentManager: FragmentManager? = null
    // 是否已经保存了状态
    private var mStateSaved = false

    private val mDoAfterStateRestored = LinkedList<Runnable>()

    private val mBackStackChangedListener = FragmentManager.OnBackStackChangedListener { onFragmentBackStackChanged() }

    protected val handler = Handler(Looper.getMainLooper(), Handler.Callback { msg -> this@BaseFragmentActivity.handleMessage(msg) })

    private val invalidatePropertyRunnable = Runnable { internalInvalidateProperty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateSaved = false
    }

    protected fun init(savedInstanceState: Bundle?, containerId: Int, homeFragmentId: Int) {
        mContainerId = containerId
        mFragmentManager = supportFragmentManager
        mFragmentManager!!.addOnBackStackChangedListener(mBackStackChangedListener)
        mFragments.clear()
        if (savedInstanceState != null) {
            // activity销毁后，recreate的情况下需要恢复以前创建的所有fragment
            restoreAllFragments(savedInstanceState)
        } else {
            mFragments.add(mFragmentManager!!.findFragmentById(homeFragmentId) as BaseFragment)
        }
        invalidateProperty()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mStateSaved = false
    }

    override fun onStart() {
        super.onStart()
        mStateSaved = false
    }

    override fun onResume() {
        super.onResume()
        mStateSaved = false
        while (!mDoAfterStateRestored.isEmpty()) {
            mDoAfterStateRestored.poll().run()
        }
    }

    override fun onPause() {
        super.onPause()
        //注：回调 2
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //注：回调 3
        return super.dispatchTouchEvent(event)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mStateSaved = true
        // 保存所有fragments
        for (i in mFragments.indices) {
            val f = mFragments[i]
            if (f != null) {
                val key = "f" + i
                mFragmentManager!!.putFragment(outState, key, f)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除所有pending的操作
        handler.removeCallbacksAndMessages(null)
    }

    protected fun doAfterStateRestored(runnable: Runnable) {
        if (mStateSaved) {
            mDoAfterStateRestored.offer(runnable)
        } else {
            runnable.run()
        }
    }

    // 恢复所有fragments
    private fun restoreAllFragments(savedInstanceState: Bundle) {
        val keys = savedInstanceState.keySet()
        for (key in keys) {
            if (key.startsWith("f")) {
                val index = Integer.parseInt(key.substring(1))
                val f = mFragmentManager?.getFragment(savedInstanceState, key) as BaseFragment
                while (mFragments.size <= index) {
                    mFragments.add(null!!)
                }
                mFragments[index] = f
            }
        }
    }

    /**
     * 将fragment压入堆栈<BR></BR>

     * @param fragment fragment
     */
    fun push(fragment: BaseFragment) {
        doAfterStateRestored(Runnable {
            mFragments.add(fragment)
            val ft = mFragmentManager!!.beginTransaction()
            ft.add(mContainerId, fragment, fragment.javaClass.simpleName)
            // 将本次操作加入回退堆栈，以便用户按back键时做回退
            ft.addToBackStack(null)
            ft.commit()
            mFragmentManager!!.executePendingTransactions()
        })
    }

    /**
     * 将栈顶的fragment出栈<BR></BR>
     */
    fun pop() {
        doAfterStateRestored(Runnable { mFragmentManager!!.popBackStackImmediate() })
    }

    fun popAll() {
        doAfterStateRestored(Runnable {
            for (i in mFragments.size - 1 downTo 1) {
                mFragments[i].pop()
            }
        })
    }

    /**
     * 根据指定类型找到已经存在的fragment实例，并将其上其他页面关闭

     * @param fragmentClass 指定的fragment的Class
     * *
     * @param <T>           所有继承自XmFragment的类型
     * *
     * @return 是否存在已经打开的实例
    </T> */
    fun <T : BaseFragment> popToExistedInstance(fragmentClass: Class<T>): Boolean {
        var existedInstanceIndex = -1
        for (i in mFragments.indices.reversed()) {
            if (mFragments[i].javaClass == fragmentClass) {
                existedInstanceIndex = i
                break
            }
        }
        val alreadyExisted = existedInstanceIndex != -1
        if (alreadyExisted) {
            for (i in mFragments.size - 1 downTo existedInstanceIndex + 1) {
                mFragments[i].pop()
            }
        }
        return alreadyExisted
    }

    val currentFragment: BaseFragment?
        get() {
            val fragmentSize = mFragments.size
            return if (fragmentSize > 0) mFragments[mFragments.size - 1] else null
        }

    val fragments: List<BaseFragment>
        get() = mFragments

    fun getFragmentBelow(fragment: BaseFragment): BaseFragment? {
        val fragments = fragments
        val index = fragments.indexOf(fragment)
        return if (index >= 1) fragments[index - 1] else null
    }

    /**
     * 判断fragment是否在最顶部<BR></BR>

     * @param fragment fragment
     * *
     * @return 是否在最顶部
     */
    fun isTopFragment(fragment: BaseFragment): Boolean {
        return mFragments.indexOf(fragment) == mFragments.size - 1
    }

    private fun onFragmentBackStackChanged() {
        // 当系统pop出加入回退堆栈的fragment的时候，需要将其从我们的容器中移除，以达到两者同步的目的
        // 因为HomeFragment不需要在回退堆栈中，所以系统中的总堆栈大小始终比mFragment的大小小1.
        val backStackEntryCount = mFragmentManager!!.backStackEntryCount
        while (mFragments.size - 1 > backStackEntryCount) {
            // 如果大于系统堆栈大小，需要将容器的顶部fragment移除，以达到两者同步的目的·
            mFragments.removeAt(mFragments.size - 1)
        }
        invalidateProperty()
    }

    override fun onBackPressed() {
        val current = currentFragment
        if (current == null || !currentFragment!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    /**
     * 使当前TitleBar失效，并重新创建<BR></BR>
     */
    fun invalidateProperty() {
        handler.removeCallbacks(invalidatePropertyRunnable)
        handler.post(invalidatePropertyRunnable)
    }

    private fun internalInvalidateProperty() {
        val fragment = currentFragment ?: return
        onPropertyChanged(fragment)
    }

    protected fun onPropertyChanged(fragment: BaseFragment) {}

    protected fun handleMessage(msg: Message): Boolean {
        return false
    }
}
