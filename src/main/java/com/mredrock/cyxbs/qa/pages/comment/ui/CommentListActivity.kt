package com.mredrock.cyxbs.qa.pages.comment.ui

import android.app.Activity
import android.os.Bundle
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.dialog.ReportDialog
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.pages.comment.viewmodel.CommentListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.utils.setPraise
import kotlinx.android.synthetic.main.qa_activity_comment_list.*
import kotlinx.android.synthetic.main.qa_comment_new_publish_layout.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.startActivityForResult

@Route(path = QA_COMMENT_LIST)
class CommentListActivity : BaseViewModelActivity<CommentListViewModel>() {
    companion object {
        const val REQUEST_CODE = 0x123

        fun activityStart(activity: Activity,
                          question: Question,
                          answer: Answer) {
            val title = "<font color=\"#7195fa\">#${question.tags}#</font>${question.title}"
            val showAdoptIcon = question.hasAdoptedAnswer || !question.isSelf
            activity.startActivityForResult<CommentListActivity>(REQUEST_CODE,
                    "qid" to question.id,
                    "title" to title,
                    "showAdoptIcon" to showAdoptIcon,
                    "isEmotion" to question.isEmotion,
                    "answer" to answer,
                    "answerNum" to answer.commentNum,
                    "praiseNum" to answer.praiseNum,
                    "isPraised" to answer.isPraised
            )
        }
    }

    override val viewModelClass = CommentListViewModel::class.java
    override val isFragmentActivity = false

    private lateinit var headerAdapter: CommentListHeaderRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var commentListRvAdapter: CommentListRvAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_comment_list)
        val title = intent.getStringExtra("title")
        val showAdoptIcon = intent.getBooleanExtra("showAdoptIcon", false)
        val isEmotion = intent.getBooleanExtra("isEmotion", false)
        initToolbar()
        initRv(title, showAdoptIcon, isEmotion)
        initCommentSheet()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener { finish() }
        val answerNub = intent.getStringExtra("answerNum")
        qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, answerNub)
        qa_ib_toolbar_more.setOnClickListener {
            ReportDialog(this).apply {
                setType(resources.getStringArray(R.array.qa_title_type)[1])
                pressReport = {
                    viewModel.reportAnswer(it)
                }
                viewModel.backPreActivityReportAnswerEvent.observeNotNull {
                    dismiss()
                }
            }.show()
        }
    }

    private fun initRv(title: String, showAdoptIcon: Boolean, isEmotion: Boolean) {
        headerAdapter = CommentListHeaderRvAdapter(title, isEmotion, showAdoptIcon)
        emptyRvAdapter = EmptyRvAdapter("还没有评论哦~")
        commentListRvAdapter = CommentListRvAdapter(isEmotion)
        footerRvAdapter = FooterRvAdapter { viewModel.retryFailedListRequest() }
        val adapterWrapper = RvAdapterWrapper(commentListRvAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_comment_list.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@CommentListActivity)
            adapter = adapterWrapper
        }
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateCommentList() }
        observeListChangeEvent()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun adoptAnswer(event: AdoptAnswerEvent) {
        viewModel.adoptAnswer(event.aId)
    }


    private fun observeListChangeEvent() = viewModel.apply {
        answerLiveData.observeNotNull { headerAdapter.refreshData(listOf(it)) }

        commentList.observe { commentListRvAdapter.submitList(it) }

        networkState.observeNotNull { footerRvAdapter.refreshData(listOf(it)) }

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
        refreshPreActivityEvent.observeNotNull {
            tv_comment_praise.setPraise(answerLiveData.value?.praiseNum, answerLiveData.value?.isPraised)
        }
    }

    private fun initCommentSheet() {
        et_new_comment.apply {
            inputType = TYPE_TEXT_FLAG_MULTI_LINE
            singleLine = false
            setOnEditorActionListener { p0, p1, p2 ->
                if (p1 == EditorInfo.IME_ACTION_SEND) {
                    viewModel.sendComment(et_new_comment.text.toString())
                    et_new_comment.text = null
                }
                false
            }
        }
        tv_comment_praise.apply {
            val praiseNum = intent.getStringExtra("praiseNum")
            val isPraised = intent.getBooleanExtra("isPraised", false)
            setPraise(praiseNum, isPraised)
            setOnClickListener { viewModel.clickPraiseButton() }
        }
    }

    override fun getViewModelFactory(): ViewModelProvider.Factory? {
        val qid = intent.getStringExtra("qid")
        val answer = intent.getParcelableExtra<Answer>("answer")

        return CommentListViewModel.Factory(qid, answer)
    }
}
