package com.mredrock.cyxbs.qa.pages.question.ui


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.question.viewmodel.QuestionListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_list.*
import kotlinx.android.synthetic.main.qa_fragment_question_list.view.*

/**
 * Created By jay68 on 2018/8/22.
 */
class QuestionListFragment : BaseViewModelFragment<QuestionListViewModel>() {
    override val openStatistics: Boolean
        get() = false

    override val viewModelClass = QuestionListViewModel::class.java
    lateinit var title: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.qa_fragment_question_list, container, false)
        val questionListRvAdapter = QuestionListRvAdapter()
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = questionListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        observeLoading(questionListRvAdapter, footerRvAdapter, emptyRvAdapter)
        return root.apply {
            rv_question_list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapterWrapper
            }
            swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateQuestionList() }
        }
    }

    private fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                               footerRvAdapter: FooterRvAdapter,
                               emptyRvAdapter: EmptyRvAdapter) = viewModel.apply {
        questionList.observe { questionListRvAdapter.submitList(it) }

        networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }


    override fun getViewModelFactory() = QuestionListViewModel.Factory(title)
}
