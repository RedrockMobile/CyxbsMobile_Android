package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.NOTICE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleDetailAdapter
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_dynamic.*
import kotlinx.android.synthetic.main.qa_fragment_last_hot.*

/**
 *@Date 2020-11-23
 *@Time 21:51
 *@author SpreadWater
 *@description  用于最新和热门的fragment的基类
 */
abstract class BaseCircleDetailFragment<T : DynamicListViewModel> : BaseViewModelFragment<T>() {
    //TODO 通过viewModel去获取最新和热门的数据。
    var onPopWindowClickListener: ((String, Dynamic) -> Unit)? = null
    var onPraiseClickListener: ((Int, Dynamic) -> Unit)? = null

    override fun getViewModelFactory() = DynamicListViewModel.Factory("main")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_last_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDynamic()
    }

    private fun initDynamic() {
        val dynamicListRvAdapter = DynamicAdapter {dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onPraiseClickListener = { position, dynamic ->
                viewModel.clickPraiseButton(position, dynamic)
                viewModel.refreshPreActivityEvent.observeNotNull {
                    notifyItemChanged(it)
                }
            }

            onPopWindowClickListener = { string, dynamic ->
                when (string) {
                    IGNORE -> {
                        viewModel.ignore(dynamic.postId)
                    }
                    REPORT -> {
                        viewModel.report(dynamic.postId, dynamic.content)
                    }

                    NOTICE -> {
                        viewModel.followCircle(dynamic.topic)
                        viewModel.getMyCirCleData()
                    }
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