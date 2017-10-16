/*
 * 文件名: BaseTabActivity.java
 * 版    权：  Copyright Paitao Tech. Co. Ltd. All Rights Reserved.
 * 描    述: TAB 基类
 * 创建人: zhaozeyang
 * 创建时间:2014-10-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.basic.tab

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TabHost
import android.widget.TextView

import com.demon.huitu.R


/**
 * TAB 基类<BR></BR>

 * @author zhaozeyang
 */
abstract class BaseTabActivity : FragmentActivity() {

    private var mContentClazzes: Array<Class<*>> = emptyArray()

    /**
     * TabHost
     */
    private var mTabHost: TabHost? = null

    /**
     * Tab管理类，用来添加fragment到每个tab中
     */
    private var mTabManager: TabManager? = null

    private var mIndicatorViews: Array<TabIndicatorView?> = emptyArray()

    /**
     * TAB IDS
     */
    private var mTabIds: Array<String?> = emptyArray()

    /**
     * 当前可见的fragment
     */
    /**
     * 获得当前可见fragment<BR></BR>

     * @return 当前可见的fragment
     */
    protected var visbileFragment: Fragment? = null
        private set

    private var mOnTabChangedListener: OnTabChangedListener? = null

    private var mContentActionListener: PerformContentActionListener? = null

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(layoutId())
        initContentActionListener()
        initTabHost()
        setTabContent()
    }

    private fun setTabContent() {
        // 获得需要展示的数据内容
        mContentClazzes = contentClazzes()

        // widget 标题
        val titles = tabTitles()

        // widget 图标
        val icons = tabIcons()

        if (null != titles && mContentClazzes!!.size != titles.size) {
            throw RuntimeException("标题数目与展示的标签数目不同")
        }
        if (null != icons && mContentClazzes!!.size != icons.size) {
            throw RuntimeException("标题数目与展示的标签图标数目不同")
        }

        mIndicatorViews = arrayOfNulls<TabIndicatorView>(mContentClazzes.size)
        mTabIds = arrayOfNulls<String>(mContentClazzes.size)

        for (i in mContentClazzes!!.indices) {
            mIndicatorViews[i] = getIndicatorView(if (null != titles && titles.size > 0)
                titles[i]
            else
                "",
                    if (null != icons && icons.size > 0) icons[i] else -1)
            mTabIds[i] = getFragmentTag(i)
            mTabManager?.addTab(mTabHost!!.newTabSpec(mTabIds[i])
                    .setIndicator(mIndicatorViews[i]),
                    mContentClazzes[i], null)
        }
        visbileFragment = mTabManager?.getFragmentById(mTabIds[0] ?: "")
        mTabManager?.setOnTabChangedListener(TabHost.OnTabChangeListener {
            fun onContentTabChanged(tabId: String) {
                visbileFragment = mTabManager?.getFragmentById(tabId)
                if (null != mOnTabChangedListener) {
                    mOnTabChangedListener!!.onTabChanged(tabId)
                }
                val fragment = mTabManager!!.getFragmentById(tabId)
                if (fragment is ContentAction) {
                    fragment.setContentActionListener(mContentActionListener)
                }
            }
        })

        // 设置默认的显示页面
        mTabManager?.onTabChanged(mTabIds[0])
    }

    protected abstract fun layoutId(): Int

    /**
     * 页签内容<BR></BR>

     * @return 显示页签内容的fragments
     */
    protected abstract fun contentClazzes(): Array<Class<*>>

    /**
     * 页签指示器标题<BR></BR>

     * @return 指示器标题
     */
    protected abstract fun tabTitles(): Array<String>?

    /**
     * 页签指示器图标<BR></BR>

     * @return 指示器图标
     */
    protected fun tabIcons(): IntArray? {
        return null
    }

    /**
     * 图标相对文字的方向<BR></BR>

     * @return 图标相对文字的方向
     */
    protected fun tabIconDirection(): Int {
        return DRAWABLE_TOP
    }

    /**
     * 根据id获取fragment<BR></BR>

     * @param tag fragment的ID
     * *
     * @return fragment
     */
    protected fun getTabById(tag: String): Fragment {
        return mTabManager!!.getFragmentById(tag)
    }

    /**
     * 根据索引获取fragment<BR></BR>

     * @param index 索引
     * *
     * @return fragment
     */
    protected fun getTabByIndex(index: Int): Fragment {
        return mTabManager!!.getFragmentById(getTabId(index))
    }

    /**
     * 根据tabId获取索引值<BR></BR>

     * @param tabId tabId
     * *
     * @return 索引
     */
    protected fun getIndexByTabId(tabId: String): Int {
        for (i in mTabIds!!.indices) {
            if (TextUtils.equals(tabId, mTabIds!![i])) {
                return i
            }
        }
        return -1
    }

    /**
     * 设置页签切换的事件监听<BR></BR>
     */
    protected fun setOnTabChangedListener(listener: OnTabChangedListener) {
        mOnTabChangedListener = listener
    }

    /**
     * 切换显示的tab<BR></BR>

     * @param index 显示的tab索引
     */
    protected fun setCurrentTab(index: Int) {
        mTabHost!!.currentTab = index
    }

    /**
     * 当页签切换<BR></BR>

     * @param tabId 页签ID
     */
    protected fun onContentTabChanged(tabId: String) {
        mTabManager!!.onTabChanged(tabId)
    }

    /**
     * 根据索引获得页签ID<BR></BR>

     * @param index 索引
     * *
     * @return 页签
     */
    protected fun getTabId(index: Int): String {
        if (index >= mTabIds.size) {
            throw IndexOutOfBoundsException("当前索引超过显示的页签个数")
        }
        return mTabIds[index] as String
    }

    /**
     * 设置指示器数字<BR></BR>

     * @param index 指示器索引
     * *
     * @param count 个数
     */
    protected fun setIndicatorCount(index: Int, count: Int) {
        mIndicatorViews[index]?.unreadCount = count
    }

    /**
     * 获得指示器数字<BR></BR>

     * @param index 索引
     * *
     * @return 指示器数字
     */
    protected fun getIndicatorCount(index: Int): Int {
        return if (null == mIndicatorViews[index])  0 else mIndicatorViews[index]!!.unreadCount
    }

    /**
     * 显示或者隐藏新消息标志视图<BR></BR>

     * @param index 索引
     * *
     * @param show 是否显示
     */
    protected fun showOrDissmissFlag(index: Int, show: Boolean) {
        mIndicatorViews[index] ?: mIndicatorViews[index]?.showOrDismissFlag(show)
    }

    private fun getFragmentTag(index: Int): String {
        return TAGS_PREFIX + mContentClazzes!![index].name
    }

    private fun getIndicatorView(title: String, drawableId: Int): TabIndicatorView {
        return TabIndicatorView(this, title, drawableId,tabIconDirection())
    }

    private fun initTabHost() {
        mTabHost = findViewById(android.R.id.tabhost) as TabHost
        mTabHost!!.setup()

        mTabManager = TabManager(this, mTabHost as TabHost, R.id.real_tabcontent)
    }

    private fun initContentActionListener() {
        mContentActionListener = object : PerformContentActionListener {

            override fun performAction(msg: Message) {
                performContentAction(msg)
            }
        }
    }

    protected fun performContentAction(msg: Message) {

    }


    @SuppressLint("InflateParams")
    inner class TabIndicatorView @JvmOverloads constructor(context: Context,
                                                           /**
                                                            * 标题字符串
                                                            */
                                                           private val mTabTitleStr: String,
                                                           /**
                                                            * 标志资源ID
                                                            */
                                                           private val mDrawableId: Int = -1,
                                                           /**
                                                            * 标志方向
                                                            */
                                                           private val mDrawableDirection: Int = DRAWABLE_TOP) : RelativeLayout(context) {

        /**
         * 标题视图
         */
        private var mTitleView: TextView? = null

        /**
         * 新消息视图
         */
        private var mNewFlagView: ImageView? = null

        /**
         * 计数视图
         */
        private var mCountView: TextView? = null

        /**
         * 计数
         */
        var unreadCount: Int = 0
            set(count) {
                field = count
                mCountView!!.visibility = if (unreadCount > 0) View.VISIBLE else View.GONE
                mCountView!!.text = count.toString()
            }

        init {
            initView(context)
        }

        private fun initView(context: Context) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.tab_indicator, null)
            mTitleView = view.findViewById(R.id.tab_title) as TextView
            mCountView = view.findViewById(R.id.tab_unread_msg) as TextView
            mNewFlagView = view.findViewById(R.id.tab_new_flag) as ImageView
            mTitleView!!.text = mTabTitleStr

            if (mDrawableId > 0) {
                when (mDrawableDirection) {
                    DRAWABLE_TOP -> mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(0,
                            mDrawableId,
                            0,
                            0)
                    DRAWABLE_LEFT -> mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(mDrawableId,
                            0,
                            0,
                            0)
                    DRAWABLE_RIGHT -> mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,
                            mDrawableId,
                            0)
                    DRAWABLE_BOTTOM -> mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,
                            0,
                            mDrawableId)
                    else -> mTitleView!!.setCompoundDrawablesWithIntrinsicBounds(0,
                            mDrawableId,
                            0,
                            0)
                }
            }
            addView(view, RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT))
            unreadCount = 0
        }

        fun showOrDismissFlag(show: Boolean) {
            mNewFlagView!!.visibility = if (show) View.VISIBLE else View.GONE
        }

    }

    companion object {
        /**
         * TAG 前缀
         */
        private val TAGS_PREFIX = "main_tab_"
        /**
         * 图标方向 左
         */
        private val DRAWABLE_LEFT = 0x01

        /**
         * 图标方向 上
         */
        private val DRAWABLE_TOP = 0x02

        /**
         * 图标方向 右
         */
        private val DRAWABLE_RIGHT = 0x03

        /**
         * 图标方向 下
         */
        private val DRAWABLE_BOTTOM = 0x04
    }

    /**
     * tab切换监听<BR></BR>

     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2014-10-6]
     */
    interface OnTabChangedListener {
        fun onTabChanged(tabId: String)
    }

    /**
     * 用于处理子页面事件的接口<BR></BR>

     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2014-11-1]
     */
    interface PerformContentActionListener {
        // 消息类型的定义
        // 为避免多个子页面的消息处理的类型重复[Message 的 what 值重复]，强烈建议在统一的位置给每个子页面的action定义类型
        fun performAction(msg: Message)
    }

    /**
     * 用于实现设置页面事件的接口<BR></BR>

     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2014-11-3]
     */
    interface ContentAction {
        fun setContentActionListener(listener: PerformContentActionListener?)
    }

}
