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
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedFragment : BaseViewModelFragment<QuestionSearchedViewModel>() {

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

        val footerRvAdapter = FooterRvAdapter {
            viewModel.retry()
        }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_search_no_result))

        swipe_refresh_layout_searching.setOnRefreshListener {
            viewModel.invalidateQuestionList()
        }
        rv_searched_question.layoutManager = LinearLayoutManager(context)
        btn_no_result_ask_question.setOnSingleClickListener {
            context?.doIfLogin("提问") {
                QuizActivity.activityStart(this, "迎新生", REQUEST_LIST_REFRESH_ACTIVITY)
                this.onDetach()
                activity?.finish()
            }
        }
    }

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)
}