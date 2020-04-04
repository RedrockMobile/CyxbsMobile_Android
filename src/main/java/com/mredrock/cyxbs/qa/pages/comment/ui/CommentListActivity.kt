package com.mredrock.cyxbs.qa.pages.comment.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.config.IS_ANSWER
import com.mredrock.cyxbs.common.config.IS_COMMENT
import com.mredrock.cyxbs.common.config.NAVIGATE_FROM_WHERE
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.event.OpenShareCommentEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.answer.ui.dialog.ReportDialog
import com.mredrock.cyxbs.qa.pages.comment.viewmodel.CommentListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.CommonDialog
import com.mredrock.cyxbs.qa.utils.setPraise
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_activity_comment_list.*
import kotlinx.android.synthetic.main.qa_comment_new_publish_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*

@Route(path = QA_COMMENT_LIST)
class CommentListActivity : BaseActivity() {
    companion object {
        const val REQUEST_CODE = 0x123
        const val PARAM_QUESTION = "question"
        const val PARAM_ANSWER = "answer"

        fun activityStart(activity: Activity,
                          question: Question,
                          answer: Answer, options: Bundle? = Bundle()) {
            activity.startActivityForResult(Intent(activity, CommentListActivity::class.java).apply {
                putExtra(PARAM_QUESTION, question)
                putExtra(PARAM_ANSWER, answer)
            }, REQUEST_CODE, options)
        }
    }

    private lateinit var intentBack: Intent
    private lateinit var viewModel: CommentListViewModel
    private var progressDialog: ProgressDialog? = null
    private fun initProgressBar() = indeterminateProgressDialog(message = "Loading...") {
        setOnDismissListener { viewModel.onProgressDialogDismissed() }
    }

    override val isFragmentActivity = false

    private lateinit var headerAdapter: CommentListHeaderRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var commentListRvAdapter: CommentListRvAdapter
    private lateinit var answerReportDialog: ReportDialog

    private lateinit var answer: Answer
    private lateinit var question: Question


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_comment_list)
        intentBack = Intent()
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null && intent.getParcelableExtra<Answer>(PARAM_ANSWER) != null) {
            answer = intent.getParcelableExtra(PARAM_ANSWER)
            question = intent.getParcelableExtra(PARAM_QUESTION)
            initViewModel(question.id, answer)
            initToolbar()
            initRv(!question.isSelf, question.isAnonymous)
            initCommentSheet()
        }
    }

    private fun initViewModel(questionId: String, answer: Answer) {
        viewModel = ViewModelProvider(this, CommentListViewModel.Factory(questionId, answer)).get(CommentListViewModel::class.java)
        viewModel.apply {
            toastEvent.observe { str -> str?.let { toast(it) } }
            longToastEvent.observe { str -> str?.let { longToast(it) } }
            progressDialogEvent.observe {
                it ?: return@observe
                if (it != ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != true) {
                    progressDialog = progressDialog ?: initProgressBar()
                    progressDialog?.show()
                } else if (it == ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != false) {
                    progressDialog?.dismiss()
                }
            }
        }
        answerReportDialog = createAnswerReportDialog()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener { finish() }
        val commentNub = answer.commentNum
        qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, commentNub)
        if (intent.getIntExtra(NAVIGATE_FROM_WHERE, IS_COMMENT) == IS_ANSWER) {
            cl_toolbar_root.addView(TextView(this).apply {
                layoutParams = qa_ib_toolbar_more.layoutParams
                text = getString(R.string.qa_answer_show_question)
                textColor = ContextCompat.getColor(this@CommentListActivity, R.color.levelTwoFontColor)
                textSize = 15f
                visible()
                setOnClickListener {
                    this@CommentListActivity.startActivity<AnswerListActivity>(AnswerListActivity.PARAM_QUESTION to question)
                    this@CommentListActivity.finish()
                }
            })
            qa_ib_toolbar_more.gone()
        } else {
            if (!answer.isSelf) {
                qa_ib_toolbar_more.setOnClickListener {
                    answerReportDialog.show()
                }
            } else {
                qa_ib_toolbar_more.gone()
            }
        }
    }

    private fun initRv(showAdoptIcon: Boolean, questionAnonymous: Boolean) {
        headerAdapter = CommentListHeaderRvAdapter(showAdoptIcon).apply {
            onAdoptClickListener = { aId ->
                if (!question.hasAdoptedAnswer) {
                    if (question.disappearAt.toDate().time > System.currentTimeMillis()) {
                        CommonDialog(this@CommentListActivity).apply {
                            initView(
                                    icon = R.drawable.qa_icon_answer_accept,
                                    title = getString(R.string.qa_answer_whether_accept_title),
                                    firstNotice = getString(R.string.qa_answer_whether_accept_notice),
                                    secondNotice = null,
                                    buttonText = getString(R.string.qa_answer_list_accept),
                                    confirmListener = View.OnClickListener {
                                        viewModel.adoptAnswer(aId)
                                        dismiss()
                                    },
                                    cancelListener = View.OnClickListener {
                                        dismiss()
                                    })
                        }.show()
                    } else toast(getString(R.string.qa_answer_adopt_late))
                } else toast(getString(R.string.qa_answer_adopted_other_answer))
            }
        }
        emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_comment_list_no_comment_hint))
        commentListRvAdapter = CommentListRvAdapter(questionAnonymous).apply {
            onReportClickListener = { commentId ->
                ReportDialog(this@CommentListActivity).apply {
                    setType(resources.getStringArray(R.array.qa_title_type)[2])
                    pressReport = {
                        viewModel.reportComment(it, commentId)
                    }
                    viewModel.reportAnswerEvent.observeNotNull {
                        dismiss()
                    }
                }.show()
            }
        }
        footerRvAdapter = FooterRvAdapter { viewModel.retryFailedListRequest() }
        val adapterWrapper = RvAdapterWrapper(commentListRvAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_comment_list.apply {
            layoutManager = LinearLayoutManager(this@CommentListActivity)
            adapter = adapterWrapper
        }
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateCommentList() }
        observeListChangeEvent()
    }


    private inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@CommentListActivity, Observer { onChange(it) })

    private inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@CommentListActivity, Observer {
        it ?: return@Observer
        onChange(it)
    })

    private fun observeListChangeEvent() = viewModel.apply {
        answerLiveData.observeNotNull {
            qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, it.commentNum)
            headerAdapter.refreshData(listOf(it))
        }
        backAndRefreshAnswerAdoptedEvent.observeNotNull {
            intentBack.putExtra(AnswerListActivity.REQUEST_REFRESH_QUESTION_ADOPTED, 1)
            setResult(Activity.RESULT_OK, intentBack)
        }
        backAndRefreshAnswerEvent.observeNotNull {
            intentBack.putExtra(AnswerListActivity.REQUEST_REFRESH_ANSWER_REFRESH, 1)
            setResult(Activity.RESULT_OK, intentBack)
        }


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
        praiseEvent.observeNotNull {
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
            setPraise(answer.praiseNum, answer.isPraised)
            setOnClickListener {
                if (viewModel.isDealing) {
                    toast(getString(R.string.qa_answer_praise_dealing))
                } else {
                    viewModel.clickPraiseButton()
                }
            }
        }
    }

    private fun createAnswerReportDialog() = ReportDialog(this).apply {
        setType(resources.getStringArray(R.array.qa_title_type)[1])
        pressReport = {
            viewModel.reportAnswer(it)
        }
        viewModel.reportAnswerEvent.observeNotNull {
            dismiss()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun openShareComment(event: OpenShareCommentEvent) {
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null && intent.getParcelableExtra<Question>(PARAM_ANSWER) != null) {
            EventBus.getDefault().removeStickyEvent(event)
        } else {
            var answerWrapper: RedrockApiWrapper<Answer>? = null

            try {
                answerWrapper = Gson().fromJson<RedrockApiWrapper<Answer>>(event.answerJson,
                        object : TypeToken<RedrockApiWrapper<Answer>>() {}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (answerWrapper == null) {
                toast(getString(R.string.qa_answer_from_mine_loading_error))
                finish()
            } else if (!answerWrapper.isSuccessful) {
                toast(answerWrapper.info ?: getString(R.string.qa_loading_from_mine_unknown_error))
                finish()
            } else {
                answer = answerWrapper.data
                initViewModel(event.questionId, answerWrapper.data)
                viewModel.getQuestionInfo()
                viewModel.questionData.observe {
                    if (it == null) finish()
                    question = it!!
                    initRv(!it.isSelf, it.isAnonymous)
                    initToolbar()
                    initCommentSheet()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }
}
