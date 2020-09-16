package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.question.ui.adapter.QuestionListRvAdapter
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedFragment : BaseViewModelFragment<QuestionSearchedViewModel>() {
    override val viewModelClass: Class<QuestionSearchedViewModel>
        get() = QuestionSearchedViewModel::class.java

    companion object {
        const val SEARCH_KEY = "searchKey"
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x3
        const val REQUEST_CODE_TO_ANSWER_List = 0x5
    }

    //搜索关键词
    private var searchKey = ""

    fun refreshSearchKey(newSearchKey: String) {
        searchKey = newSearchKey
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchKey = arguments?.getString(SEARCH_KEY) ?: searchKey
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //应对被动销毁
        if (searchKey.isEmpty()) {
            searchKey = savedInstanceState?.getString(SEARCH_KEY) ?: searchKey
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_question_search_result, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY, searchKey)
    }

    fun invalidate() {
        viewModel.searchKey = searchKey
        viewModel.getKnowledge(searchKey)
        viewModel.invalidateQuestionList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.searchKey = searchKey
        viewModel.getKnowledge(searchKey)
        val headerRvAdapter = SearchResultHeaderAdapter()

        val questionListRvAdapter = QuestionListRvAdapter {
            AnswerListActivity.activityStart(this, it.id, REQUEST_CODE_TO_ANSWER_List)
        }
        val footerRvAdapter = FooterRvAdapter {
            viewModel.retry()
        }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_search_no_result))

        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = questionListRvAdapter,
                headerAdapter = headerRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        swipe_refresh_layout_searching.setOnRefreshListener {
            viewModel.invalidateQuestionList()
        }
        rv_searched_question.layoutManager = LinearLayoutManager(context)
        rv_searched_question.adapter = adapterWrapper
        btn_no_result_ask_question.setOnClickListener {
            context?.doIfLogin("提问") {
                QuizActivity.activityStart(this, "迎新生", REQUEST_LIST_REFRESH_ACTIVITY)
                this.onDetach()
                activity?.finish()
            }
        }
        observeLoading(questionListRvAdapter, headerRvAdapter, footerRvAdapter, emptyRvAdapter)
    }

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)

    private fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                               headerRvAdapter: SearchResultHeaderAdapter,
                               footerRvAdapter: FooterRvAdapter,
                               emptyRvAdapter: EmptyRvAdapter) = viewModel.apply {
        viewModel.apply {
            questionList.observe {
                questionListRvAdapter.submitList(it)
            }
            networkState.observe {
                it?.run {
                    footerRvAdapter.refreshData(listOf(this))
                }
            }

            initialLoad.observe {
                when (it) {
                    NetworkState.LOADING -> {
                        swipe_refresh_layout_searching.isRefreshing = true
                        emptyRvAdapter.showHolder(3)
                    }
                    NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                        swipe_refresh_layout_searching.isRefreshing = false
                    }
                    else -> {
                        swipe_refresh_layout_searching.isRefreshing = false
                        emptyRvAdapter.hideHolder()
                    }
                }
            }
            knowledge.observe {
                if (it != null) {
                    headerRvAdapter.refreshData(listOf(it))
                    rv_searched_question.smoothScrollToPosition(0)
                }
            }
        }
    }
}