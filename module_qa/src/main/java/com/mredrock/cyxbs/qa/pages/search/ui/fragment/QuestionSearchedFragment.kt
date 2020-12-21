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
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchKnowledgeAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchNoResultAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedFragment : BaseViewModelFragment<QuestionSearchedViewModel>() {

    companion object {
        const val SEARCH_KEY = "searchKey"
    }

    //搜索关键词
    private var searchKey = ""
    lateinit var dynamicListRvAdapter: DynamicAdapter
    var emptyRvAdapter: SearchNoResultAdapter? = null
    var footerRvAdapter: FooterRvAdapter? = null

    fun refreshSearchKey(newSearchKey: String) {
        searchKey = newSearchKey
    }

    fun invalidate() {
        viewModel.searchKey = searchKey
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
        return inflater.inflate(R.layout.qa_fragment_question_search_result, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY, searchKey)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInitialView()
        viewModel.searchKey = searchKey
        swipe_refresh_layout_searching.isRefreshing = true
        emptyRvAdapter?.showHolder(3)
        initResultView()
        viewModel.isCreateOver.observe {
            if (it != null) {
                if (it) {
                    //加载完成
                    LogUtils.d("zt", "加载完成！")
                    swipe_refresh_layout_searching.isRefreshing = false
                    emptyRvAdapter?.hideHolder()
                    if (viewModel.questionList.value!=null){
                        qa_tv_contract_content.visibility=View.VISIBLE
                    }
                    if (viewModel.isKnowledge) {
                        //知识库不为空时候显示
                        qa_line.visibility = View.VISIBLE
                        qa_rv_knowledge.visibility = View.VISIBLE
                        qa_tv_knowledge.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initInitialView() {
        viewModel.getKnowledge(searchKey)
        dynamicListRvAdapter = DynamicAdapter(context) { dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onPopWindowClickListener = { position, string, dynamic ->
                when (string) {
                    CommentConfig.IGNORE -> {
                        viewModel.ignore(dynamic)
                    }
                    CommentConfig.REPORT -> {
                        activity?.let {
                            QaReportDialog.show(it) { reportContent ->
                                viewModel.report(dynamic, reportContent)
                            }
                        }
                    }
                    CommentConfig.NOTICE -> {
                        viewModel.followCircle(dynamic)
                    }
                    CommentConfig.DELETE -> {
                        activity?.let { it1 ->
                            QaDialog.show(it1, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                                viewModel.deleteId(dynamic.postId, "0")
                            }
                        }
                    }
                }
            }
        }
        emptyRvAdapter = SearchNoResultAdapter(getString(R.string.qa_search_no_result)) {
            context?.doIfLogin("提问") {
                QuizActivity.activityStart(this, "发动态", RELEASE_DYNAMIC_ACTIVITY_REQUEST)
                this.onDetach()
                activity?.finish()
            }
        }
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = dynamicListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        rv_searched_question.layoutManager = LinearLayoutManager(context)
        rv_searched_question.adapter = adapterWrapper


    }

    private fun initResultView() {
        viewModel.questionList.observe {
            dynamicListRvAdapter.submitList(it)
        }
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter?.refreshData(listOf(this))
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
        swipe_refresh_layout_searching.setOnRefreshListener {
            viewModel.invalidateSearchQuestionList()
        }
        viewModel.deleteTips.observe {
            viewModel.invalidateSearchQuestionList()
        }
        viewModel.followCircle.observe {
            viewModel.invalidateSearchQuestionList()
        }

        viewModel.knowledge.observe {
            if (it != null) {
                val flexBoxManager = FlexboxLayoutManager(BaseApp.context)
                flexBoxManager.flexWrap = FlexWrap.WRAP
                qa_rv_knowledge.layoutManager = flexBoxManager

                qa_rv_knowledge.adapter = SearchKnowledgeAdapter {
                    //邮问知识库的调用
                }.apply {
                    addData(it)
                }
            } else {
                LogUtils.d("zt", "知识数据库为空")
                qa_rv_knowledge.gone()
                qa_line.gone()
                qa_tv_knowledge.gone()
            }
        }
    }
}