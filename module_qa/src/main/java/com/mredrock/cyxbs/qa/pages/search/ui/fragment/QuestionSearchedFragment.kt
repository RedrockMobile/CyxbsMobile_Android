package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchKnowledgeAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchNoResultAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*
import kotlinx.android.synthetic.main.qa_item_search_no_result.*
import kotlinx.android.synthetic.main.qa_recycler_item_comment.*

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
    var headerRvAdapter : SearchResultHeaderAdapter ?=null
    var emptyRvAdapter:SearchNoResultAdapter?=null
    fun refreshSearchKey(newSearchKey: String) {
        searchKey = newSearchKey
    }

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)
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
        return inflater.inflate(R.layout.qa_fragment_question_search_result,container,false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY, searchKey)

    }

    fun invalidate() {
        viewModel.searchKey = searchKey
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInitialView()
        viewModel.searchKey = searchKey
        viewModel.getSearchDynamic(searchKey)
        swipe_refresh_layout_searching.isRefreshing = true
        emptyRvAdapter?.showHolder(3)
        viewModel.isCreateOver.observe {
            it?.let {
                if (it){
                    swipe_refresh_layout_searching.isRefreshing=false
                    emptyRvAdapter?.hideHolder()
                    initResultView()
                }
            }
        }
    }

    private fun initInitialView(){
        headerRvAdapter = SearchResultHeaderAdapter {
        }
        emptyRvAdapter = SearchNoResultAdapter(getString(R.string.qa_search_no_result)){
            context?.doIfLogin("提问") {
                QuizActivity.activityStart(this, "发动态", REQUEST_LIST_REFRESH_ACTIVITY)
                this.onDetach()
                activity?.finish()
            }
        }

        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = headerRvAdapter!!,
                emptyAdapter = emptyRvAdapter
        )
        rv_searched_question.layoutManager = LinearLayoutManager(context)
        rv_searched_question.adapter = adapterWrapper
    }
    private fun initResultView() {
        if (!viewModel.SEARCH_RESULT_IS_EMPTY) {
            qa_tv_contract_content.visibility = View.VISIBLE
            qa_line.visibility = View.VISIBLE
            qa_rv_knowledge.visibility = View.VISIBLE
            qa_tv_knowledge.visibility = View.VISIBLE
            if(!viewModel.isKnowledge){
                //知识数据库为空
                LogUtils.d("zt","知识数据库为空")
                qa_rv_knowledge.gone()
                qa_line.gone()
                qa_tv_knowledge.gone()
            }
        }
        swipe_refresh_layout_searching.setOnRefreshListener {
            viewModel.getSearchDynamic(searchKey)
        }
        viewModel.searchedDynamic.observe {
            if (it != null) {
                headerRvAdapter?.refreshData(it)
            }
        }
        viewModel.knowledge.observe {
            if (it!=null&&!viewModel.SEARCH_RESULT_IS_EMPTY){
                LogUtils.d("zt","知识库数据不为空")
                val flexBoxManager = FlexboxLayoutManager(BaseApp.context)
                flexBoxManager.flexWrap = FlexWrap.WRAP
                qa_rv_knowledge.layoutManager=flexBoxManager
                qa_rv_knowledge.adapter=SearchKnowledgeAdapter({
                    //邮问知识库的调用
                }).apply {
                    addData(it)
                }
            }
        }
        viewModel.initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout_searching.isRefreshing = true
                    (rv_searched_question.adapter as? RvAdapterWrapper)?.apply {

                    }
                    emptyRvAdapter?.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    swipe_refresh_layout_searching.isRefreshing = false
                }
                else -> {
                    swipe_refresh_layout_searching.isRefreshing = false
                    emptyRvAdapter?.hideHolder()
                }
            }
        }
    }
}