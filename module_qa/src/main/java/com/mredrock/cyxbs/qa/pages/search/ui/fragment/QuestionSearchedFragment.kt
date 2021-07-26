package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.COPY_LINK
import com.mredrock.cyxbs.qa.config.CommentConfig.DELETE
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_FRIEND
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_ZONE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.config.RequestResultCode.ClickKnowledge
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState.Companion.CANNOT_LOAD_WITHOUT_LOGIN
import com.mredrock.cyxbs.qa.network.NetworkState.Companion.LOADING
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource.Companion.SEARCHRESULT
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchKnowledgeAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchNoResultAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchResultHeaderAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*
import kotlinx.android.synthetic.main.qa_recycler_knowledge_detail.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class QuestionSearchedFragment : BaseViewModelFragment<QuestionSearchedViewModel>() {


    companion object {
        const val SEARCH_KEY = "searchKey"
    }

    private var mTencent: Tencent? = null

    //搜索关键词
    private var searchKey = ""
    lateinit var dynamicListRvAdapter: DynamicAdapter
    var knowledges: List<Knowledge>? = null
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
        initResultView()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
        viewModel.isCreateOver.observe {
            if (it != null) {
                if (it) {
                    //加载完成
                    if (SEARCHRESULT || viewModel.isKnowledge) {
                        //有数据的刷新
                        swipe_refresh_layout_searching.isRefreshing = false
                        emptyRvAdapter?.showResultRefreshHolder()
                    } else {
                        //没有数据的刷新
                        swipe_refresh_layout_searching.isRefreshing = false
                        emptyRvAdapter?.showNOResultRefreshHolder()
                    }
                    if (SEARCHRESULT) {
                        qa_tv_contract_content.visibility = View.VISIBLE
                        this.context?.apply {
                            rv_searched_question.background=ContextCompat.getDrawable(this,R.drawable.qa_shape_comment_header_background)
                        }
                    }
                    if (viewModel.isKnowledge) {
                        //知识库不为空时候显示
                        if (ClickKnowledge) {
                            qa_line.gone()
                            qa_tv_knowledge.gone()
                        } else {
                            qa_line.visibility = View.VISIBLE
                            qa_rv_knowledge.visibility = View.VISIBLE
                            qa_tv_knowledge.visibility = View.VISIBLE
                        }
                    } else {
                        qa_line.gone()
                        qa_tv_knowledge.gone()
                    }
                }
            }
        }
    }

    private fun initInitialView() {
        viewModel.getKnowledge(searchKey)
        dynamicListRvAdapter = DynamicAdapter(this.requireContext()) { dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onShareClickListener = { dynamic, mode ->
                val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                when (mode) {
                    QQ_FRIEND -> {
                        mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@QuestionSearchedFragment, dynamic.topic, dynamic.content, url, pic) }
                    }
                    QQ_ZONE ->
                        mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@QuestionSearchedFragment, dynamic.topic, dynamic.content, url, arrayListOf(pic)) }
                    COPY_LINK -> {
                        this@QuestionSearchedFragment.context?.let {
                            ClipboardController.copyText(it, url)
                        }
                    }
                }
            }
            onPopWindowClickListener = { position, string, dynamic ->
                when (string) {
                    IGNORE -> {
                        viewModel.ignore(dynamic)
                    }
                    REPORT -> {
                        this@QuestionSearchedFragment.context?.let {
                            QaReportDialog(it).apply {
                                show { reportContent ->
                                    viewModel.report(dynamic, reportContent)

                                }
                            }.show()
                        }
                    }
                    DELETE -> {
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
        viewModel.searchKey = searchKey
        swipe_refresh_layout_searching.isRefreshing = true
        emptyRvAdapter?.showInitialHolder(3)
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
                LOADING -> {
                    swipe_refresh_layout_searching.isRefreshing = true
                    (rv_searched_question.adapter as? RvAdapterWrapper)?.apply {
                    }

                    emptyRvAdapter?.showInitialHolder(3)
                }
                CANNOT_LOAD_WITHOUT_LOGIN -> {
                    swipe_refresh_layout_searching.isRefreshing = false
                }
                else -> {
                    if (SEARCHRESULT || viewModel.isKnowledge) {
                        //有数据的刷新
                        swipe_refresh_layout_searching.isRefreshing = false
                        emptyRvAdapter?.showResultRefreshHolder()
                    } else {
                        //没有数据的刷新
                        swipe_refresh_layout_searching.isRefreshing = false
                        emptyRvAdapter?.showNOResultRefreshHolder()
                    }
                }
            }
        }
        swipe_refresh_layout_searching.setOnRefreshListener {
            if (ClickKnowledge) {
                //点击知识库时的刷新
                viewModel.invalidateSearchQuestionList()
                qa_line.gone()
                qa_tv_knowledge.gone()
            } else {
                viewModel.invalidateSearchQuestionList()
            }
        }
        viewModel.deleteTips.observe {
            if (it == true)
                viewModel.invalidateSearchQuestionList()
        }
        viewModel.ignorePeople.observe {
            if (it == true)
                viewModel.invalidateSearchQuestionList()
        }

        viewModel.knowledge.observe {
            if (!it.isNullOrEmpty()) {
                knowledges=it
                val adapterKnowledge=SearchKnowledgeAdapter(qa_rv_knowledge)
                val adapterSearchResultHeader=SearchResultHeaderAdapter(adapterKnowledge,qa_rv_knowledge)
                adapterKnowledge.searchResultHeaderAdapter=adapterSearchResultHeader

                val flexBoxManager = FlexboxLayoutManager(BaseApp.context)
                flexBoxManager.flexWrap = FlexWrap.WRAP
                qa_rv_knowledge.layoutManager = flexBoxManager
                qa_rv_knowledge.adapter = adapterKnowledge
                knowledges?.let { it1 ->
                    adapterKnowledge.addData(it1)
                }
                qa_rv_knowledge.layoutManager = flexBoxManager
            } else {
                qa_rv_knowledge.gone()
                qa_line.gone()
                qa_tv_knowledge.gone()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            DYNAMIC_DETAIL_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateSearchQuestionList()
                } else {
                    // 不需要刷新，则更新当前的dynamic为详细页的dynamic（避免出现评论数目不一致的问题）
                    dynamicListRvAdapter.curSharedItem?.apply {
                        val dynamic = data?.getParcelableExtra<Dynamic>("refresh_dynamic")
                        dynamic?.let {
                            dynamicListRvAdapter.curSharedDynamic?.commentCount = dynamic.commentCount
                            this.findViewById<TextView>(R.id.qa_tv_dynamic_comment_count).text = it.commentCount.toString()
                        }
                    }
                    dynamicListRvAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}