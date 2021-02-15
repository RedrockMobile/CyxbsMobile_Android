package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_last_hot.*

/**
 *@Date 2020-11-23
 *@Time 21:51
 *@author SpreadWater
 *@description  用于最新和热门的fragment的基类
 */
abstract class BaseCircleDetailFragment<T : DynamicListViewModel> : BaseViewModelFragment<T>() {

    private var mTencent: Tencent? = null

    override fun getViewModelFactory() = DynamicListViewModel.Factory("main")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_last_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDynamic()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
    }

    private fun initDynamic() {
        val dynamicListRvAdapter = DynamicAdapter(this.requireContext()) { dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onShareClickListener = { dynamic, mode ->
                when (mode) {
                    CommentConfig.QQ_FRIEND ->
                        mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@BaseCircleDetailFragment, dynamic.topic, dynamic.content, "https://cn.bing.com/", "") }
                    CommentConfig.QQ_ZONE ->
                        mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@BaseCircleDetailFragment, dynamic.topic, dynamic.content, "https://cn.bing.com/", ArrayList()) }
                    CommentConfig.COPY_LINK ->
                        ClipboardController.copyText(this@BaseCircleDetailFragment.requireContext(), "https://cn.bing.com/")
                }
            }
            onPopWindowClickListener = { position, string, dynamic ->
                when (string) {
                    IGNORE -> {
                        viewModel.ignore(dynamic)
                    }
                    REPORT -> {
                        this@BaseCircleDetailFragment.activity?.let {
                            QaReportDialog.show(it) { reportContent ->
                                viewModel.report(dynamic, reportContent)
                            }
                        }
                    }

                    CommentConfig.DELETE -> {
                        this@BaseCircleDetailFragment.activity?.let { it1 ->
                            QaDialog.show(it1, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                                viewModel.deleteId(dynamic.postId, "0")
                            }
                        }
                    }
                }
                viewModel.deleteTips.observeNotNull {
                    notifyItemRemoved(position)
                }
            }
        }
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = dynamicListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        qa_rv_circle_detail_last_hot.adapter = adapterWrapper
        qa_rv_circle_detail_last_hot.layoutManager = LinearLayoutManager(context)

        qa_hot_last_swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidateQuestionList()
        }
        viewModel.dynamicList.observe {
            dynamicListRvAdapter.submitList(it)
        }
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }
        viewModel.initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = true
                    (qa_rv_circle_detail_last_hot.adapter as? RvAdapterWrapper)?.apply {
                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = false
                }
                else -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }
}