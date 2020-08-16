package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.event.SearchEvent
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchHistoryRvAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchHotVpAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_search.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchingFragment : BaseFragment() {


    private val historyRvAdapter = SearchHistoryRvAdapter {
        EventBus.getDefault().post(SearchEvent(historyData[it]))
    }

    private val historyData = List<String>(6) { "测试用例" }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_question_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        observeLoading()
    }

    private fun initView() {
        tb_question_search.apply {
            setupWithViewPager(vp_question_search)
            setSelectedTabIndicator(R.drawable.qa_ic_question_tab_indicator)
        }
        rv_question_search_history.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyRvAdapter
        }
    }

    //todo 等后端
    private fun observeLoading() {
        val titles = listOf<String>("热门", "新生热门")
        val data = List<String>(5) { "测试用例" }
        val fragments = List(titles.size) { SearchHotWordFragment.newInstance(data) }
        vp_question_search.adapter = SearchHotVpAdapter(fragments, titles, childFragmentManager)
        rl_question_searched.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rl_question_searched.viewTreeObserver.removeOnPreDrawListener(this)
                val layoutParams = rl_question_searched.layoutParams
                layoutParams.height = (context?.dp2px(50f))!! * (data.size + 1)
                rl_question_searched.layoutParams = layoutParams
                return true
            }
        })

        historyRvAdapter.refreshData(historyData)
    }
}