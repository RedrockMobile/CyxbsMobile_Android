package com.mredrock.cyxbs.mine.page.feedback.center.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackCenterBinding
import com.mredrock.cyxbs.mine.page.feedback.center.adapter.FeedbackCenterAdapter
import com.mredrock.cyxbs.mine.page.feedback.center.presenter.FeedbackCenterPresenter
import com.mredrock.cyxbs.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel
import com.mredrock.cyxbs.mine.page.feedback.edit.ui.FeedbackEditActivity

/**
 * @Date : 2021/8/23   20:51
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterActivity :
        BaseMVPVMActivity<FeedbackCenterViewModel, MineActivityFeedbackCenterBinding, FeedbackCenterPresenter>() {
    private val mAdapter by lazy {
        FeedbackCenterAdapter()
    }

    override fun createPresenter(): FeedbackCenterPresenter {
        return FeedbackCenterPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.mine_activity_feedback_center
    }
    override fun initView() {
        mAdapter.setEventHandler(EventHandler())
        binding?.mineRecyclerview?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    override fun observeData() {
        viewModel.apply {
            contentList.observe({lifecycle},{
                mAdapter.setData(it)
            })
        }
    }

    override fun initListener() {
        binding?.apply {
            btnQuestion.setOnClickListener {
                startActivity<FeedbackEditActivity>()
            }
        }
    }

    inner class EventHandler{
        fun onItemClick(itemView: View){
            startActivity<FeedbackDetailActivity>()
        }
    }
}