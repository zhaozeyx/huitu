/*
 * 文件名: BasicPullToRefreshFragment
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/8/10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.basic

import android.os.Bundle
import android.view.View

import com.demon.huitu.ui.basic.pulltorefresh.PullToRefreshListView

/**
 * [一句话功能简述]<BR></BR>
 * [功能详细描述]

 * @author zhaozeyang
 * *
 * @version [Taobei Client V20160411, 16/8/10]
 */
abstract class BasicPullToRefreshFragment : BasicFragment() {
    protected var currentPage = START_PAGE
        private set

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showInitProgress()
        listView.setPageCount(PAGE_COUNT)
        listView.setScrollToLoadListener(object : PullToRefreshListView.ScrollToLoadListener {
            override fun onPullUpLoadData() {
                loadData()
            }

            override fun onPullDownLoadData() {
                currentPage = START_PAGE
                loadData()
            }
        }, LOAD_MORE_VALUE)
        loadData()
    }

    protected fun showInitProgress() {
        if (null != initProgress) {
            initProgress!!.visibility = View.VISIBLE
        }
    }

    protected fun dismissInitProgress() {
        if (null != initProgress) {
            initProgress!!.visibility = View.GONE
        }
    }


    protected abstract val listView: PullToRefreshListView

    protected abstract fun loadData()

    protected abstract val initProgress: View?

    protected fun resetRefreshStatus() {
        listView.resetPullStatus()
    }

    protected fun onRefreshLoaded(hasNew: Boolean) {
        if (hasNew) {
            currentPage++
        }
        listView.setPullUpToRefresh(hasNew)
    }

    protected fun resetCurrentPage() {
        currentPage = START_PAGE
    }

    companion object {
        protected val PAGE_COUNT = 10
        protected val START_PAGE = 1
        private val LOAD_MORE_VALUE = 3
    }

}
