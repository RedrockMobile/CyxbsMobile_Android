package com.mredrock.cyxbs.qa.pages.question.ui


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.main.QuestionContainerFragment
import com.mredrock.cyxbs.qa.pages.question.viewmodel.QuestionListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_fragment_question_list.*
import kotlinx.android.synthetic.main.qa_fragment_question_list.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/22.
 */
class QuestionListFragment : BaseViewModelFragment<QuestionListViewModel>() {
    override val openStatistics: Boolean
        get() = false

    override val viewModelClass = QuestionListViewModel::class.java
    var title: String = ""

    // 用户每次切换登陆状态，该flag需要重置
    private var isFirstTimeLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (title.isBlank()) {
            title = savedInstanceState?.getString("title") ?: ""
        }
        val root = inflater.inflate(R.layout.qa_fragment_question_list, container, false)
        val questionListRvAdapter = QuestionListRvAdapter(this)
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = questionListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        observeLoading(questionListRvAdapter, footerRvAdapter, emptyRvAdapter)
        return root.apply {
            rv_question_list.apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                adapter = adapterWrapper
            }
            swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateQuestionList() }
        }
    }

    private fun observeLoading(questionListRvAdapter: QuestionListRvAdapter,
                               footerRvAdapter: FooterRvAdapter,
                               emptyRvAdapter: EmptyRvAdapter) = viewModel.apply {
        questionList.observe { questionListRvAdapter.submitList(it) }

        networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        isFirstTimeLoad = true
        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    (rv_question_list.adapter as? RvAdapterWrapper)?.apply {

                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN->{
                    swipe_refresh_layout.isRefreshing = false
                    if (isFirstTimeLoad) {
                        isFirstTimeLoad = false
                    } else {
                        EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用邮问哦~"))
                    }
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY  && resultCode == Activity.RESULT_OK) {
            if (title == Question.ALL || title == data!!.getStringExtra("type")) {
                viewModel.invalidateQuestionList()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (title.isNotBlank()) {
            outState.putString("title", title)
        }
    }

    override fun getViewModelFactory() = QuestionListViewModel.Factory(title)

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        viewModel.invalidateQuestionList()
        isFirstTimeLoad = true
    }
}
