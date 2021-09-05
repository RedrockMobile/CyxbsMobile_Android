package com.mredrock.cyxbs.mine.page.feedback.center.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackCenterBinding
import com.mredrock.cyxbs.mine.page.feedback.center.adapter.FeedbackCenterAdapter
import com.mredrock.cyxbs.mine.page.feedback.center.presenter.FeedbackCenterPresenter
import com.mredrock.cyxbs.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel
import com.mredrock.cyxbs.mine.page.feedback.edit.ui.FeedbackEditActivity
import com.mredrock.cyxbs.mine.page.feedback.center.ui.FeedbackDetailActivity
import com.mredrock.cyxbs.mine.page.feedback.history.list.HistoryListActivity
import com.mredrock.cyxbs.mine.page.security.util.Jump2QQHelper

/**
 * @Date : 2021/8/23   20:51
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterActivity :
    BaseMVPVMActivity<FeedbackCenterViewModel, MineActivityFeedbackCenterBinding, FeedbackCenterPresenter>() {
    /**
     * 初始化adapter
     */
    private val mAdapter by lazy {
        FeedbackCenterAdapter()
    }

    /**
     * 获取P层
     */
    override fun createPresenter(): FeedbackCenterPresenter {
        return FeedbackCenterPresenter()
    }

    /**
     * 获取布局信息
     */
    override fun getLayoutId(): Int {
        return R.layout.mine_activity_feedback_center
    }

    /**
     * 初始化view
     */
    override fun initView() {
        mAdapter.setEventHandler(EventHandler())
        binding?.mineRecyclerview?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    /**
     * 监听数据
     */
    override fun observeData() {
        viewModel.apply {
            contentList.observe({ lifecycle }, {
                mAdapter.setData(it)
            })
        }
    }

    /**
     * 初始化listener
     */
    override fun initListener() {
        binding?.apply {
            fabCenterBack.setOnSingleClickListener {
                onBackPressed()
            }
            btnQuestion.setOnClickListener {
                startActivity<FeedbackEditActivity>()
            }
            ivHistory.setOnSingleClickListener {
                startActivity<HistoryListActivity>()
            }
            tvQqTwo.setOnSingleClickListener {
                Jump2QQHelper.onFeedBackClick(
                    this@FeedbackCenterActivity
                )
            }
        }
    }

    /**
     * 每个item的监听事件 通过dataBinding传递
     */
    inner class EventHandler {
        var position: Int = 0
        fun onItemClick(itemView: View, title: String, content: String) {
            val intent =
                Intent(this@FeedbackCenterActivity, FeedbackDetailActivity::class.java).apply {
                    putExtra("title", title)
                    putExtra("content", content)
                }
            startActivity(intent)
        }
    }
}