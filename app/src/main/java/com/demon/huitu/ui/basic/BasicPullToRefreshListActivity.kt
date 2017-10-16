package com.demon.huitu.ui.basic

/*
 * 文件名: BasicPullToRefreshListActivity
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/5
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.BaseAdapter
import android.widget.ProgressBar

import com.demon.huitu.R
import com.demon.huitu.ui.basic.pulltorefresh.PullToRefreshListView

import java.util.ArrayList

abstract class BasicPullToRefreshListActivity<T> : BasicActivity() {
    protected var currentPage = START_PAGE
        private set

    protected abstract var listView: PullToRefreshListView
    protected var mData: MutableList<T> = ArrayList()

    private var mTitleBar: Toolbar? = null
    private var mProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_pull_to_refresh_list)
        initViews()
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
        showInitProgress()
        loadData()
    }

    private fun initViews() {
        listView = findViewById(R.id.list) as PullToRefreshListView
        mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        mTitleBar = findViewById(R.id.titleBar) as Toolbar
        mTitleBar!!.setNavigationOnClickListener { finish() }
        showInitProgress()
    }

    protected fun showInitProgress() {
        mProgressBar!!.visibility = View.VISIBLE
    }

    protected fun dismissInitProgress() {
        mProgressBar!!.visibility = View.GONE
    }

    protected fun setTitleBarTitle(title: Int) {
        mTitleBar!!.setTitle(title)
    }

    protected abstract fun loadData()

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

    protected abstract val adapter: BaseAdapter

    protected fun updateData(list: List<T>) {
        if (currentPage == START_PAGE) {
            mData.clear()
            mData.addAll(list)
        } else {
            mData.addAll(list)
        }
        adapter.notifyDataSetChanged()
        onRefreshLoaded(list.size >= PAGE_COUNT)
    }

    companion object {
        protected val PAGE_COUNT = 10
        protected val START_PAGE = 1
        private val LOAD_MORE_VALUE = 3
    }
}
