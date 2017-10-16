package com.demon.huitu.ui.task

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demon.huitu.R
import kotlinx.android.synthetic.main.fragment_task.*

/*
 * 文件名: TaskFragment
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
class TaskFragment : Fragment() {
    lateinit var adapter: FragmentPagerAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater?.inflate(R.layout.fragment_task, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val titles = resources.getStringArray(R.array.task_titles)
        adapter = TaskPagerAdapter(childFragmentManager, titles)
        pager.adapter = adapter
        pager.offscreenPageLimit = titles.size
        tab.setupWithViewPager(pager)
        for (i in titles.indices) {
            tab.getTabAt(i)?.text = titles[i]
        }
    }

    private inner class TaskPagerAdapter(fm: FragmentManager, private val mTitles: Array<String>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return TaskListFragment.newInstance(TaskListFragment.TYPE_UNTREAT)
                1 -> return TaskListFragment.newInstance(TaskListFragment.TYPE_TREATED)
                else -> return null
            }
        }

        override fun getCount(): Int {
            return mTitles.size
        }
    }
}