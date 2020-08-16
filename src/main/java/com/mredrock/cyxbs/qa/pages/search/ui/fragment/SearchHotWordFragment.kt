package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.event.SearchEvent
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchHotRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_search_hot_word_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchHotWordFragment : BaseFragment() {
    private var hotWordList: ArrayList<String>? = null

    companion object {
        private const val HOT_WORD_List = "hot_word"
        private const val KIND = "kind"
        fun newInstance(titles: List<String>): SearchHotWordFragment = SearchHotWordFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(HOT_WORD_List, ArrayList(titles))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (hotWordList.isNullOrEmpty()) {
            hotWordList = arguments?.getStringArrayList(HOT_WORD_List)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (hotWordList.isNullOrEmpty()) {
            hotWordList = arguments?.getStringArrayList(HOT_WORD_List)
        }
        return inflater.inflate(R.layout.qa_fragment_search_hot_word_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_search_hot_word.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchHotRvAdapter {
                EventBus.getDefault().post(hotWordList?.get(it)?.let { it1 -> SearchEvent(it1) })
            }.apply {
                hotWordList?.let { refreshData(it) }
            }
        }
    }
}