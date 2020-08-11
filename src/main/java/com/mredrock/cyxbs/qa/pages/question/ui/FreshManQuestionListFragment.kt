package com.mredrock.cyxbs.qa.pages.question.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderRvAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderVpAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.QuestionListRvAdapter
import com.mredrock.cyxbs.qa.pages.question.viewmodel.FreshManQuestionListViewModel
import com.mredrock.cyxbs.qa.pages.question.viewmodel.QuestionListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter

/**
 * Created by yyfbe, Date on 2020/8/11.
 */
class FreshManQuestionListFragment : BaseQuestionListFragment<FreshManQuestionListViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        fun newInstance(title: String): FreshManQuestionListFragment = FreshManQuestionListFragment().apply {
            arguments = Bundle().apply { putString(FRAGMENT_TITLE, title) }
        }
    }

    private val headerVpAdapter = FreshManHeaderVpAdapter {

    }
    override var headerRvAdapter: FreshManHeaderRvAdapter? = FreshManHeaderRvAdapter(headerVpAdapter)
    override val viewModelClass: Class<FreshManQuestionListViewModel> = FreshManQuestionListViewModel::class.java
    override fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                                headerRvAdapter: FreshManHeaderRvAdapter?,
                                footerRvAdapter: FooterRvAdapter,
                                emptyRvAdapter: EmptyRvAdapter): QuestionListViewModel = viewModel.apply {
        super.observeLoading(questionListRvAdapter, headerRvAdapter, footerRvAdapter, emptyRvAdapter)
        freshManQuestionList.observe { data ->
            if (data == null) return@observe
            headerVpAdapter.submitList(data)
        }
        //提醒
        headerRvAdapter?.refreshData(listOf(0))
    }

    override fun getViewModelFactory() = FreshManQuestionListViewModel.Factory(Question.LIFE, title)
}