package com.mredrock.cyxbs.qa.pages.comment.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.AnswerDetail
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.answer.ui.dialog.ReportDialog
import com.mredrock.cyxbs.qa.pages.comment.viewmodel.CommentListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.CommonDialog
import com.mredrock.cyxbs.qa.utils.setPraise
import kotlinx.android.synthetic.main.qa_activity_comment_list.*
import kotlinx.android.synthetic.main.qa_comment_new_publish_layout.*

@Route(path = QA_COMMENT_LIST)
class CommentListActivity : BaseActivity() {
    companion object {
        const val REQUEST_CODE = 0x123

        fun activityStart(activity: Activity,
                          answerId: String) {
            activity.startActivityForResult<CommentListActivity>(REQUEST_CODE,
                    QA_PARAM_ANSWER_ID to answerId)

        }
    }

    private lateinit var intentBack: Intent
    private lateinit var viewModel: CommentListViewModel
    private var progressDialog: ProgressDialog? = null
    private fun initProgressBar() = ProgressDialog(this).apply {
        isIndeterminate = true
        setMessage("Loading...")
        setOnDismissListener { viewModel.onProgressDialogDismissed() }
    }

    override val isFragmentActivity = false

    private lateinit var headerAdapter: CommentListHeaderRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var commentListRvAdapter: CommentListRvAdapter
    private lateinit var answerReportDialog: ReportDialog

    private var answer: AnswerDetail? = null

    private var aid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_comment_list)
        intentBack = Intent()
        if (intent.getStringExtra(QA_PARAM_ANSWER_ID) != null) {
            aid = intent.getStringExtra(QA_PARAM_ANSWER_ID)
            initViewModel(aid)
        }
    }

    private fun initViewModel(aid: String) {
        viewModel = ViewModelProvider(this, CommentListViewModel.Factory(aid)).get(CommentListViewModel::class.java)
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
            answerLiveData.observeNotNull {
                answer = it
                qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, it.commentNum.toString())
                initRv(it.answerOwner, it.questionIsAnonymous)
                initToolbar(it)
                headerAdapter.refreshData(listOf(it))
                observeListChangeEvent()
            }
        }
        answerReportDialog = createAnswerReportDialog()
    }

    private fun initToolbar(answer: AnswerDetail) {
        qa_ib_toolbar_back.setOnClickListener { finish() }
        val commentNub = answer.commentNum
        qa_tv_toolbar_title.text = baseContext.getString(R.string.qa_comment_list_comment_count, commentNub.toString())
        if (intent.getIntExtra(NAVIGATE_FROM_WHERE, IS_COMMENT) == IS_ANSWER) {
            val lay = qa_ib_toolbar_more.layoutParams
            lay.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lay.height = ViewGroup.LayoutParams.WRAP_CONTENT
            cl_toolbar_root.addView(TextView(this).apply {
                layoutParams = lay
                text = getString(R.string.qa_answer_show_question)
                setTextColor(ContextCompat.getColor(this@CommentListActivity, R.color.common_level_two_font_color))
                textSize = 15f
                visible()
                setOnClickListener {
                    this@CommentListActivity.startActivity<AnswerListActivity>(QA_PARAM_QUESTION_ID to answer.questionId)
                    this@CommentListActivity.finish()
                }
            })
            qa_ib_toolbar_more.gone()
        } else {
            if (!answer.answerOwner) {
                qa_ib_toolbar_more.setOnClickListener {
                    doIfLogin {
                        answerReportDialog.show()
                    }
                }
            } else {
                qa_ib_toolbar_more.gone()
            }
        }
    }

    private fun initRv(showAdoptIcon: Boolean, questionAnonymous: Boolean) {
        if (answer == null) return
        headerAdapter = CommentListHeaderRvAdapter(showAdoptIcon).apply {
            onAdoptClickListener = { aId ->
                if (answer!!.answerIsAdopted) {
                    if (answer!!.disappearAt > System.currentTimeMillis()) {
                        CommonDialog(this@CommentListActivity).apply {
                            initView(
                                    icon = R.drawable.qa_ic_answer_accept,
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
    }


    private inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@CommentListActivity, Observer { onChange(it) })

    private inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@CommentListActivity, Observer {
        it ?: return@Observer
        onChange(it)
    })

    private fun observeListChangeEvent() = viewModel.apply {
        backAndRefreshAnswerAdoptedEvent.observeNotNull {
            intentBack.putExtra(AnswerListActivity.REQUEST_REFRESH_QUESTION_ADOPTED, 1)
            setResult(Activity.RESULT_OK, intentBack)
        }
        backAndRefreshAnswerEvent.observeNotNull {
            intentBack.putExtra(AnswerListActivity.REQUEST_REFRESH_ANSWER_REFRESH, 1)
            setResult(Activity.RESULT_OK, intentBack)
        }


        commentList?.observeNotNull {
            commentListRvAdapter.submitList(it)
        }

        networkState?.observeNotNull {
            footerRvAdapter.refreshData(listOf(it))
        }

        initialLoad?.observe {
            it ?: return@observe
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
            tv_comment_praise.setPraise(answerLiveData.value?.praiseNum.toString(), answerLiveData.value?.answerIsPraised)
        }
        questionData.observe {

        }
    }

    private fun initCommentSheet() {
        et_new_comment.apply {
            inputType = TYPE_TEXT_FLAG_MULTI_LINE
            setSingleLine(false)
            setOnEditorActionListener { p0, p1, p2 ->
                doIfLogin {
                    if (p1 == EditorInfo.IME_ACTION_SEND) {
                        viewModel.sendComment(et_new_comment.text.toString())
                        et_new_comment.text = null
                    }
                }
                false
            }
        }
        tv_comment_praise.apply {
            setPraise(answer?.praiseNum.toString(), answer?.answerIsPraised)
            setOnClickListener {
                doIfLogin {
                    if (!viewModel.isDealing) {
                        viewModel.clickPraiseButton()
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.common_scale_fade_in_with_bezier, R.anim.common_slide_fade_out_to_bottom_with_bezier)
    }
}
