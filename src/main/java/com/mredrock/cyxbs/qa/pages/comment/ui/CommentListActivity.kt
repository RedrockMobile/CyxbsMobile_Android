package com.mredrock.cyxbs.qa.pages.comment.ui

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.pages.comment.viewmodel.CommentListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_comment_list.*
import kotlinx.android.synthetic.main.qa_comment_new_publish_layout.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivityForResult

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
                    "answerNum" to answer.commentNum
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
//        qa_tv_comment_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, answerNub)
        initToolbar()
        initRv(title, showAdoptIcon, isEmotion)
        initCommentSheet()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener { finish() }
        val answerNub = intent.getStringExtra("answerNum")
        qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, answerNub)
        qa_ib_toolbar_more.setOnClickListener {
            //todo
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
    }

    private fun initCommentSheet() {
//        LayoutInflater.from(this).inflate(R.layout.qa_comment_new_publish_layout,,false)
//                .apply {
//            val commentEditText = findViewById<EditText>(R.id.et_new_comment)

        tv_comment_prise.setOnClickListener {
            viewModel.sendComment(et_new_comment.text.toString())
            et_new_comment.text = null
//            }
        }
    }

    override fun getViewModelFactory(): ViewModelProvider.Factory? {
        val qid = intent.getStringExtra("qid")
        val answer = intent.getParcelableExtra<Answer>("answer")

        return CommentListViewModel.Factory(qid, answer)
    }
}
