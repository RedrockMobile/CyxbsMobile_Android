package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.QAHistory
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
                if (historyDataList.size > it) {
                    val history = historyDataList[it]
                    EventBus.getDefault().post(QASearchEvent(history.info))
                    history.time = System.currentTimeMillis()
                    viewModel.update(history)
                }
            },
            onCleanIconClick = {
                if (historyDataList.size > it) {
                    val history = historyDataList[it]
                    historyDataList.removeAt(it)
                    if (historyDataList.size == 0) {
                        ll_history_title.gone()
                    }
                    viewModel.delete(history)

                }
            })

    private var historyDataList = mutableListOf<QAHistory>()
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
                if (list.size == 0) {
                    ll_history_title.gone()
                } else {
                    ll_history_title.visible()
                }
                historyDataList = list
                historyRvAdapter.refreshData(list.map { it.info })
            }

        }
    }

    override val viewModelClass: Class<SearchViewModel> = SearchViewModel::class.java
}