/*
 * 文件名: PullToRefreshListView.java
 * 版    权：  Copyright Paitao Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 下拉上拉刷新的列表控件
 * 创建人: zhaozeyang
 * 创建时间:2014-4-1
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.basic.pulltorefresh

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView

import com.demon.huitu.R

/**
 * 下拉上拉刷新的列表控件<BR></BR>

 * @author zhaozeyang
 */
class PullToRefreshListView
/**
 * 构造方法

 * @param context 上下文
 * *
 * @param attrs 属性
 */
(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    /**
     * 真实展示数据的listView
     */
    /**
     * 获取展示列表数据的视图<BR></BR>

     * @return ListView
     */
    var refreshListView: ListView? = null
        private set

    private var mFooterRootView: View? = null

    /**
     * 展示刷新状态的控件
     */
    /**
     * 获得下拉刷新的布局控件<BR></BR>

     * @return SwipeRefreshLayout
     */
    var refreshLayout: SwipeRefreshLayout? = null
        private set

    /**
     * 需要加载下一页的位置
     */
    private var mScrollToLoadListener: ScrollToLoadListener? = null

    /**
     * 需要加载刷新的位置
     */
    private var mNeedLoadPosDesc = 0

    private var mPullUpToRefreshable = true

    /**
     * 是否在上拉刷新状态
     */
    private var mIsPullUpRefreshing = false

    /**
     * 每页加载数据
     */
    private var mPageCount = 20

    init {
        initView()
    }

    /**
     * 初始化view<BR></BR>
     */
    fun initView() {
        val contentView = LayoutInflater.from(context)
                .inflate(R.layout.refresh_list, null)
        val footerView = LayoutInflater.from(context)
                .inflate(R.layout.refresh_list_footer, null)
        mFooterRootView = footerView.findViewById(R.id.root_layout)
        refreshListView = contentView.findViewById(R.id.listview) as ListView
        refreshListView!!.addFooterView(footerView)
        refreshLayout = contentView.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        addView(contentView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
        setListParam()

        setRefreshLayoutParam()
    }

    private fun setRefreshLayoutParam() {
        refreshLayout!!.setOnRefreshListener(OnRefreshListener {
            // 如果当前处于上拉刷新状态，则不进行下拉刷新
            if (mIsPullUpRefreshing) {
                refreshLayout!!.isRefreshing = false
                return@OnRefreshListener
            }
            if (null != mScrollToLoadListener) {
                mScrollToLoadListener!!.onPullDownLoadData()
            }
        })
    }

    /**
     * 设置滑动加载数据监听<BR></BR>

     * @param listener 监听
     * *
     * @param needLoadPosDesc 倒数第几个进行刷新
     */
    fun setScrollToLoadListener(listener: ScrollToLoadListener,
                                needLoadPosDesc: Int) {
        mScrollToLoadListener = listener
        mNeedLoadPosDesc = if (needLoadPosDesc > 0) needLoadPosDesc else 0
    }

    /**
     * 设置是否可以上拉刷新<BR></BR>

     * @param pullUpTorefreshable 上拉刷新
     */
    fun setPullUpToRefresh(pullUpTorefreshable: Boolean) {
        mPullUpToRefreshable = pullUpTorefreshable
    }

    /**
     * 重置刷新的状态<BR></BR>
     */
    fun resetPullStatus() {
        mIsPullUpRefreshing = false
        refreshLayout!!.isRefreshing = false
        mFooterRootView!!.visibility = View.GONE
    }

    /**
     * 每页加载的数据<BR></BR>

     * @param count 每页加载的数据个数
     */
    fun setPageCount(count: Int) {
        mPageCount = count
    }

    var adapter: ListAdapter
        get() = refreshListView!!.adapter
        set(adapter) {
            refreshListView!!.adapter = adapter
        }

    private fun setListParam() {
        mFooterRootView!!.visibility = View.GONE
        refreshListView!!.setOnScrollListener(object : OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int,
                                  visibleItemCount: Int, totalItemCount: Int) {
                val dataTotalItemCount = totalItemCount
                -refreshListView!!.footerViewsCount
                -refreshListView!!.headerViewsCount
                if (dataTotalItemCount == 0 || dataTotalItemCount < mPageCount) {
                    return
                }
                // 如果第一个可见的位置+可见的item总数 = 总的ITEM数目 - 倒数第几个刷新数
                if ((totalItemCount - mNeedLoadPosDesc == firstVisibleItem + visibleItemCount || totalItemCount == firstVisibleItem + visibleItemCount) && mPullUpToRefreshable) {
                    // 如果当前未处于刷新状态
                    if (null != mScrollToLoadListener
                            && !refreshLayout!!.isRefreshing
                            && !mIsPullUpRefreshing) {
                        mIsPullUpRefreshing = true
                        mFooterRootView!!.visibility = View.VISIBLE
                        mScrollToLoadListener!!.onPullUpLoadData()
                    }
                }
            }
        })
    }

    /**
     * 滑动加载数据的监听<BR></BR>

     * @author zhaozeyang
     * *
     * @version [Paitao Client V20130911, 2014-4-2]
     */
    interface ScrollToLoadListener {
        /**
         * 上拉加载数据<BR></BR>
         */
        fun onPullUpLoadData()

        /**
         * 下拉加载数据<BR></BR>
         */
        fun onPullDownLoadData()
    }
}
