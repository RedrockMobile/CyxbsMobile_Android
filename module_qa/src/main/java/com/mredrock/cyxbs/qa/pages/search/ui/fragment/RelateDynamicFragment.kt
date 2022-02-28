package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.WindowManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.MINE_PERSON_PAGE
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.SearchNoResultAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.callback.IKeyProvider
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*
import kotlinx.android.synthetic.main.qa_fragment_question_search_result_dynamic.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/26
 * @description
 **/
class RelateDynamicFragment : BaseResultFragment() {
    //QQ分享相关
    private var mTencent: Tencent? = null

    //搜索内容
    private var searchKey = ""
    private lateinit var dynamicListRvAdapter: DynamicAdapter
    var emptyRvAdapter: SearchNoResultAdapter? = null
    var footerRvAdapter: FooterRvAdapter? = null

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)

    override fun initData() {
        //获取搜索内容
        searchKey = (requireActivity() as IKeyProvider).getKey()

        initDynamic()
        initObserve()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
        viewModel.isCreateOver.observe(viewLifecycleOwner, Observer{
            if (it != null) {
                if (it) {
                    //加载完成
                    if (SearchQuestionDataSource.SEARCH_RESULT || viewModel.isKnowledge) {
                        //有数据的刷新
                        qa_srl_search_dynamic.isRefreshing = false
                        emptyRvAdapter?.showResultRefreshHolder()
                    } else {
                        //没有数据的刷新
                        qa_srl_search_dynamic.isRefreshing = false
                        emptyRvAdapter?.showNOResultRefreshHolder()
                    }
                }
            }
        })
    }

    override fun getLayoutId() = R.layout.qa_fragment_question_search_result_dynamic

    private fun initDynamic() {
        dynamicListRvAdapter = DynamicAdapter(this.requireContext()) { dynamic, view ->
            //当进入动态详情页面时 搜索界面的window 的软键盘不允许再被弹起，写这个的原因是不知道为什么从动态详情页面返回搜索界面时软键盘会自动弹起
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onShareClickListener = { dynamic, mode ->
                val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                when (mode) {
                    CommentConfig.QQ_FRIEND -> {
                        val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                        mTencent?.let { it1 ->
                            ShareUtils.qqShare(
                                it1,
                                this@RelateDynamicFragment,
                                dynamic.topic,
                                dynamic.content,
                                url,
                                pic
                            )
                        }
                    }
                    CommentConfig.QQ_ZONE ->
                        mTencent?.let { it1 ->
                            ShareUtils.qqQzoneShare(
                                it1,
                                this@RelateDynamicFragment,
                                dynamic.topic,
                                dynamic.content,
                                url,
                                ArrayList(dynamic.pics)
                            )
                        }
                    CommentConfig.COPY_LINK -> {
                        this@RelateDynamicFragment.context?.let {
                            ClipboardController.copyText(it, url)
                        }
                    }
                }
            }
            onPopWindowClickListener = { _, string, dynamic ->
                when (string) {
                    CommentConfig.IGNORE -> {
                        viewModel.ignore(dynamic)
                    }
                    CommentConfig.REPORT -> {
                        this@RelateDynamicFragment.context?.let {
                            QaReportDialog(it).apply {
                                show { reportContent ->
                                    viewModel.report(dynamic, reportContent)

                                }
                            }.show()
                        }
                    }
                    CommentConfig.DELETE -> {
                        activity?.let { it1 ->
                            QaDialog.show(
                                it1,
                                resources.getString(R.string.qa_dialog_tip_delete_comment_text),
                                {}) {
                                viewModel.deleteDynamic(dynamic.postId)
                            }
                        }
                    }
                }
            }
            onAvatarClickListener = {
                ARouter.getInstance().build(MINE_PERSON_PAGE)
                    .withString("redid", it)
                    .navigation()
            }
        }
        emptyRvAdapter = SearchNoResultAdapter(getString(R.string.qa_search_no_result)) {
            context?.doIfLogin("提问") {
                QuizActivity.activityStart(
                    this, "发动态",
                    RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
                )
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
        qa_rv_search_dynamic.layoutManager = LinearLayoutManager(context)
        qa_rv_search_dynamic.adapter = adapterWrapper
        qa_srl_search_dynamic.isRefreshing = true
        emptyRvAdapter?.showInitialHolder(3)
    }

    private fun initObserve() {
        //动态数据
        viewModel.questionList.observe(viewLifecycleOwner, Observer{
            dynamicListRvAdapter.submitList(it)
        })


        viewModel.networkState.observe(viewLifecycleOwner, Observer{
            it?.run {
                footerRvAdapter?.refreshData(listOf(this))
            }
        })

        //首次加载状态的监听
        viewModel.initialLoad.observe(viewLifecycleOwner, Observer{
            when (it) {
                NetworkState.LOADING -> {
                    qa_srl_search_dynamic.isRefreshing = true
                    emptyRvAdapter?.showInitialHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_srl_search_dynamic.isRefreshing = false
                }
                else -> {
                    if (SearchQuestionDataSource.SEARCH_RESULT || viewModel.isKnowledge) {
                        //有数据的刷新
                        qa_srl_search_dynamic.isRefreshing = false
                        emptyRvAdapter?.showResultRefreshHolder()
                    } else {
                        //没有数据的刷新
                        qa_srl_search_dynamic.isRefreshing = false
                        emptyRvAdapter?.showNOResultRefreshHolder()
                    }
                }
            }
        })

        qa_srl_search_dynamic.setOnRefreshListener {
            viewModel.invalidateSearchQuestionList()
        }

        viewModel.deleteTips.observe(viewLifecycleOwner, Observer{
            if (it == true)
                viewModel.invalidateSearchQuestionList()
        })
        viewModel.ignorePeople.observe(viewLifecycleOwner, Observer{
            if (it == true)
                viewModel.invalidateSearchQuestionList()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            RequestResultCode.DYNAMIC_DETAIL_REQUEST -> {
                //设置软键盘为可以展示
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED)
                if (resultCode == RequestResultCode.NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateSearchQuestionList()
                } else {
                    // 不需要刷新，则更新当前的dynamic为详细页的dynamic（避免出现评论数目不一致的问题）
                    dynamicListRvAdapter.curSharedItem?.apply {
                        val dynamic = data?.getParcelableExtra<Dynamic>("refresh_dynamic")
                        dynamic?.let {
                            // 进行判断，如果返回的数据评论数和当前的不一样才回去刷新列表
                            if (dynamicListRvAdapter.curSharedDynamic?.commentCount != it.commentCount) {
                                dynamicListRvAdapter.curSharedDynamic?.commentCount =
                                    it.commentCount
                                this.findViewById<TextView>(R.id.qa_tv_dynamic_comment_count).text =
                                    it.commentCount.toString()
                                dynamicListRvAdapter.notifyItemChanged(
                                    dynamicListRvAdapter.curSharedItemPosition,
                                    ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}