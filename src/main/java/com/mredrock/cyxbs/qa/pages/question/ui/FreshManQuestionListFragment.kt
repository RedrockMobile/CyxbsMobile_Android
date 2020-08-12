package com.mredrock.cyxbs.qa.pages.question.ui

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.main.QuestionContainerFragment
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderRvAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderVpAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.QuestionListRvAdapter
import com.mredrock.cyxbs.qa.pages.question.viewmodel.FreshManQuestionListViewModel
import com.mredrock.cyxbs.qa.pages.question.viewmodel.QuestionListViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by yyfbe, Date on 2020/8/11.
 */
class FreshManQuestionListFragment : BaseQuestionListFragment<FreshManQuestionListViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        fun newInstance(title: String): FreshManQuestionListFragment = FreshManQuestionListFragment().apply {
            arguments = Bundle().apply { putString(FRAGMENT_TITLE, title) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidateQuestionList()
            viewModel.invalidateFreshManQuestionList()
        }
    }

    private val headerVpAdapter = FreshManHeaderVpAdapter {

    }
    override var headerRvAdapter: FreshManHeaderRvAdapter? = FreshManHeaderRvAdapter(headerVpAdapter) {
        context?.doIfLogin("提问") {
            QuizActivity.activityStart(this, "迎新生", QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY)
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
    override val viewModelClass: Class<FreshManQuestionListViewModel> = FreshManQuestionListViewModel::class.java
    override fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                                headerRvAdapter: FreshManHeaderRvAdapter?,
                                footerRvAdapter: FooterRvAdapter,
                                emptyRvAdapter: EmptyRvAdapter): QuestionListViewModel = viewModel.apply {
        super.observeLoading(questionListRvAdapter, headerRvAdapter, footerRvAdapter, emptyRvAdapter)
        freshManQuestionList.observe { data ->
            if (data == null) return@observe
            headerVpAdapter.submitList(data)
            headerRvAdapter?.refreshData(listOf(0))
        }
    }

    override fun getViewModelFactory() = FreshManQuestionListViewModel.Factory(Question.LIFE, title)

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop) {
            viewModel.invalidateQuestionList()
            viewModel.invalidateFreshManQuestionList()
        } else
            rv_question_list.smoothScrollToPosition(0)

    }
}