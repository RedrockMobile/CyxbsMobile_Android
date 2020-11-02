package com.mredrock.cyxbs.qa.pages.question.ui.fragment

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.main.QuestionContainerFragment
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderInnerVpAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.FreshManHeaderRvAdapter
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
class FreshManQuestionListFragment : BaseQuestionListFragment<FreshManQuestionListViewModel>() {
    companion object {
        fun newInstance(title: String): FreshManQuestionListFragment = FreshManQuestionListFragment().apply {
            arguments = Bundle().apply { putString(FRAGMENT_TITLE, title) }
        }

        const val REQUEST_CODE_TO_ANSWER_LIST = 0x4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidateQuestionList()
            viewModel.invalidateFreshManQuestionList()
        }
    }

    private val headerInnerVpAdapter = FreshManHeaderInnerVpAdapter {
        viewModel.getQuestionInfo(it.questionId)
    }
    override var headerRvAdapter: FreshManHeaderRvAdapter? = FreshManHeaderRvAdapter(headerInnerVpAdapter) {
        context?.doIfLogin("提问") {
            QuizActivity.activityStart(this, "迎新生", QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY)
        }
    }
    override fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                                headerRvAdapter: FreshManHeaderRvAdapter?,
                                footerRvAdapter: FooterRvAdapter,
                                emptyRvAdapter: EmptyRvAdapter): QuestionListViewModel = viewModel.apply {
        super.observeLoading(questionListRvAdapter, headerRvAdapter, footerRvAdapter, emptyRvAdapter)
        freshManQuestionList.observe { data ->
            headerInnerVpAdapter.submitList(data)
        }
        questionData.observe {
            if (it != null) {
                AnswerListActivity.activityStart(this@FreshManQuestionListFragment, it.id, REQUEST_CODE_TO_ANSWER_LIST)
            }
        }
    }

    override fun getViewModelFactory() = FreshManQuestionListViewModel.Factory(title)

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    override fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop) {
            viewModel.invalidateQuestionList()
            viewModel.invalidateFreshManQuestionList()
        } else
            rv_question_list.smoothScrollToPosition(0)

    }
}