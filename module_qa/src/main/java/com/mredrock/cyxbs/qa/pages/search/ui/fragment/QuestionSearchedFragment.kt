package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicPagerAdapter
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchKnowledgeAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedFragment : BaseResultFragment() {

    companion object {
        const val SEARCH_KEY = "searchKey"
    }

    init {
        isOpenLifeCycleLog = true
    }

    //搜索关键词
    private var searchKey = ""

    fun refreshSearchKey(newSearchKey: String) {
        searchKey = newSearchKey
    }

    fun invalidate() {
        viewModel.searchKey = searchKey
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY, searchKey)
    }

    override fun initData() {
        viewModel.getKnowledge(searchKey)
        initObserve()
        initPager()
    }

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)

    override fun getLayoutId() = R.layout.qa_fragment_question_search_result

    private fun initObserve(){
        viewModel.knowledge.observe(viewLifecycleOwner, Observer{
            if (!it.isNullOrEmpty()) {
                val adapterKnowledge = SearchKnowledgeAdapter(qa_rv_knowledge)
                val adapterSearchResultHeader =
                    SearchResultHeaderAdapter(adapterKnowledge, qa_rv_knowledge)
                adapterKnowledge.searchResultHeaderAdapter = adapterSearchResultHeader

                val flexBoxManager = FlexboxLayoutManager(BaseApp.appContext)
                flexBoxManager.flexWrap = FlexWrap.WRAP
                qa_rv_knowledge.layoutManager = flexBoxManager
                qa_rv_knowledge.adapter = adapterKnowledge
                it?.let { it1 ->
                    adapterKnowledge.addData(it1)
                }

//                qa_line.visibility = View.VISIBLE
                qa_rv_knowledge.visibility = View.VISIBLE
                qa_tv_knowledge.visibility = View.VISIBLE
            } else {
                qa_rv_knowledge.gone()
//                qa_line.gone()
                qa_tv_knowledge.gone()
            }
        })
    }

    private fun initPager(){
        qa_vp_search_result.adapter = DynamicPagerAdapter(requireActivity()).apply {
            addFragment(RelateDynamicFragment())
            addFragment(RelateUserFragment())
        }
        TabLayoutMediator(qa_tl_contract_content,qa_vp_search_result){ tab,position ->
            when(position){
                0 -> tab.text = "内容"
                1 -> tab.text = "用户"
            }
        }.attach()
    }
}