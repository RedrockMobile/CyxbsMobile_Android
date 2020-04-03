package com.mredrock.cyxbs.qa.pages.question.ui


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.event.RefreshQaEvent
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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created By jay68 on 2018/8/22.
 */
class QuestionListFragment : BaseViewModelFragment<QuestionListViewModel>() {

    companion object {
        const val FRAGMENT_TITLE = "title"
        fun newInstance(title: String): QuestionListFragment = QuestionListFragment().apply {
            arguments = Bundle().apply { putString(FRAGMENT_TITLE, title) }
        }
    }

    override val openStatistics: Boolean
        get() = false

    override val viewModelClass = QuestionListViewModel::class.java
    var title: String = ""

    // 判断rv是否到顶
    private var isRvAtTop = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (title.isBlank()) {
            title = arguments?.getString(FRAGMENT_TITLE).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (title.isBlank()) {
            title = arguments?.getString(FRAGMENT_TITLE).toString()
        }
        return inflater.inflate(R.layout.qa_fragment_question_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questionListRvAdapter = QuestionListRvAdapter(this)
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = questionListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        observeLoading(questionListRvAdapter, footerRvAdapter, emptyRvAdapter)
        rv_question_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterWrapper
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isRvAtTop = !recyclerView.canScrollVertically(-1)
                }
            })
        }
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateQuestionList() }
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

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    (rv_question_list.adapter as? RvAdapterWrapper)?.apply {

                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    swipe_refresh_layout.isRefreshing = false
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
        if (requestCode == QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (title == Question.NEW || title == data!!.getStringExtra("type")) {
                viewModel.invalidateQuestionList()
            }
        }
    }


    override fun getViewModelFactory() = QuestionListViewModel.Factory(title)

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop)
            viewModel.invalidateQuestionList()
        else
            rv_question_list.smoothScrollToPosition(0)

    }
}
