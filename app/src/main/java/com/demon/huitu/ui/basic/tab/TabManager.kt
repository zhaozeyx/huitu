/*
 * 文件名: TabManager.java
 * 版    权：  Copyright Paitao Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 管理添加fragment的类
 * 创建人: zhaozeyang
 * 创建时间:2013-9-22
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.basic.tab

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.TabHost
import android.widget.TabHost.OnTabChangeListener
import java.util.*

/**
 * 管理添加fragment的类<BR></BR>
 * @author zhaozeyang
 */
class TabManager
/**
 * 构造方法
 * @param activity context
 * *
 * @param tabHost tab
 * *
 * @param containerID fragment's parent note
 */
(
        /**
         * 要添加fragment TAB的activity
         */
        private val mActivity: FragmentActivity,
        /**
         * TabHost
         */
        private val mTabHost: TabHost,
        /**
         * fragment的父容器
         */
        private val mContainerID: Int) : OnTabChangeListener {

    /**
     * TAB缓存
     */
    private val mTabs = HashMap<String, TabInfo>()

    /**
     * 上一个点击的tab
     */
    private var mLastTab: TabInfo? = null

    /**
     * tab该表的监听器
     */
    private var mOnTabChangeListener: OnTabChangeListener? = null

    init {
        mTabHost.setOnTabChangedListener(this)
    }

    /**
     * Tab信息<BR></BR>
     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2013-9-18]
     */
    internal class TabInfo
    /**
     * 构造方法
     * @param tag 标签
     * *
     * @param clss Class
     * *
     * @param args 参数
     */
    (
            /**
             * 每一个Tab的Tag
             */
            val mTag: String,
            /**
             * 每个tab页签要展示的view的类的引用
             */
            val mClss: Class<*>,
            /**
             * 传入的参数
             */
            val mArgs: Bundle?) {

            /**
             * 添加的fragment
             */
            var mFragment: Fragment? = null
    }

    /**
     * 创建tab页签的工厂类<BR></BR>
     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2013-9-18]
     */
    internal class TabFactory private constructor(private val mContext: Context) : TabHost.TabContentFactory {

        override fun createTabContent(tag: String): View {
            val v = View(mContext)
            v.minimumHeight = 0
            v.minimumWidth = 0
            return v
        }

        companion object {
            private var sInstance: TabFactory? = null

            fun getInstance(context: Context): TabFactory {
                if (sInstance == null) {
                    sInstance = TabFactory(context)
                }
                return sInstance as TabFactory
            }
        }
    }

    /**
     * 添加页签<BR></BR>
     * @param tabSpec TabSpec
     * *
     * @param clss Class
     * *
     * @param args Bundle
     */
    fun addTab(tabSpec: TabHost.TabSpec, clss: Class<*>, args: Bundle?) {
        //      tabSpec.setContent(new TabFactory(mActivity));
        tabSpec.setContent(TabFactory.getInstance(mActivity))
        val tag = tabSpec.tag

        var info = TabInfo(tag, clss, args)

        val fm = mActivity.supportFragmentManager
        //      final FragmentManager fm = mActivity.getFragmentManager();
        info.mFragment = fm.findFragmentByTag(tag)

        info.mFragment.let {
            if(it != null) {
                if (!it.isDetached) {
                    val ft = fm.beginTransaction()
                    ft.hide(it)
                    ft.commit()
                }
            }
        }
        mTabs.put(tag, info)
        mTabHost.addTab(tabSpec)
    }

    /**
     * 根据ID获得对应的fragment<BR></BR>
     * @param tabId 页签ID
     * *
     * @return fragment
     */
    fun getFragmentById(tabId: String): Fragment {
        val tabInfo = mTabs[tabId]
        return tabInfo?.mFragment as Fragment
    }

    /**
     * 每次都重新建
     * @param tabId 页签ID
     */
    override fun onTabChanged(tabId: String?) {
        val newTab = mTabs[tabId]
        if (mLastTab != newTab) {
            val fragmentManager = mActivity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // 脱离之前的tab
            if (mLastTab != null && mLastTab!!.mFragment != null) {
                fragmentTransaction.hide(mLastTab!!.mFragment)
            }
            if (newTab != null) {
                if (newTab.mFragment == null) {
                    newTab.mFragment = Fragment.instantiate(mActivity,
                            newTab.mClss.name,
                            newTab.mArgs)
                    fragmentTransaction.add(mContainerID,
                            newTab.mFragment,
                            newTab.mTag)
                } else {
                    fragmentTransaction.show(newTab.mFragment)
                }
            }
            mLastTab = newTab
            fragmentTransaction.commitAllowingStateLoss()
            // 会在进程的主线程中，用异步的方式来执行,如果想要立即执行这个等待中的操作，就要调用这个方法
            // 所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
            fragmentManager.executePendingTransactions()
            invokeOnTabChangeListener(tabId)
        }
    }

    /**
     * Register a callback to be invoked when the selected state of any of the items
     * in this list changes
     * @param l
     * * The callback that will run
     */
    fun setOnTabChangedListener(l: OnTabChangeListener) {
        mOnTabChangeListener = l
    }

    private fun invokeOnTabChangeListener(tabId: String?) {
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener!!.onTabChanged(tabId)
        }
    }

    companion object {

        /**
         * TAG
         */
        private val TAG = "TabManager"
    }
}
