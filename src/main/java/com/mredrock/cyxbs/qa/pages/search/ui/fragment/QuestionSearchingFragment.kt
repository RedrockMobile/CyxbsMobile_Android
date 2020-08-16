package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.event.QASearchEvent
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchHistoryRvAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.qa_fragment_question_search.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchingFragment : BaseViewModelFragment<SearchViewModel>() {


    private val historyRvAdapter = SearchHistoryRvAdapter(
            onHistoryClick = {
                val history = viewModel.historyFromDB.value?.get(it)
                if (history != null) {
                    EventBus.getDefault().post(QASearchEvent(history.info))
                    history.time = System.currentTimeMillis()
                    viewModel.update(history)
                }
            },
            onCleanIconClick = { viewModel.historyFromDB.value?.get(it)?.let { history -> viewModel.delete(history) } }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_question_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        viewModel.getHistoryFromDB()
        observeLoading()
    }

    private fun initView() {
        rv_question_search_history.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyRvAdapter
        }
        tv_question_search_history_clear.setOnClickListener {
            viewModel.deleteAll()
        }
    }

    private fun observeLoading() {
        viewModel.historyFromDB.observe { list ->
            if (list != null) {
                historyRvAdapter.refreshData(list.map { it.info })
            }

        }
    }

    override val viewModelClass: Class<SearchViewModel> = SearchViewModel::class.java
}