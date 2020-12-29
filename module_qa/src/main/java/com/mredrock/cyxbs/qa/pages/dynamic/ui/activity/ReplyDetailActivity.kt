package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.ReplyDetailAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_reply_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*


/**
 *@author zhangzhe
 *@date 2020/12/15
 *@description 复用DynamicDetailViewModel的详细界面
 */

class ReplyDetailActivity : AppCompatActivity() {

    companion object {
        var viewModel: DynamicDetailViewModel? = null
        var dataList: List<Comment>? = null
        fun activityStart(activity: Activity, vm: DynamicDetailViewModel, data: List<Comment>) {
            activity.apply {
                window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                startActivityForResult(
                        Intent(this, ReplyDetailActivity::class.java),
                        RequestResultCode.REPLY_DETAIL_REQUEST
                )
                viewModel = vm
                dataList = data
            }
        }
    }

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }

    private val footerRvAdapter = FooterRvAdapter { refresh() }

    private val replyDetailAdapter = ReplyDetailAdapter({_,_->}, {_,_->})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_reply_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }

        initToolbar()
        initReplyList()
    }

    private fun initReplyList() {

        qa_reply_detail_swipe_refresh.setOnRefreshListener {
            refresh()
        }

        refresh()

        qa_reply_detail_rv_reply_list.apply {
            layoutManager = LinearLayoutManager(context)

            val adapterWrapper = RvAdapterWrapper(
                    normalAdapter = replyDetailAdapter,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
            adapter = adapterWrapper
        }
    }
    private fun initToolbar() {
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_reply_detail_title_text)
        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
    }

    fun refresh() {
        replyDetailAdapter.refreshData(listOf())
        dataList?.toMutableList()?.let { replyDetailAdapter.refreshData(it) }
        qa_reply_detail_swipe_refresh.isRefreshing = false
    }
}