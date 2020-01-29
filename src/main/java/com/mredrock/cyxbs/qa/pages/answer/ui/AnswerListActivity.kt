package com.mredrock.cyxbs.qa.pages.answer.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.OpenShareQuestionEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.dialog.ReportDialog
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerListViewModel
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.pages.comment.ui.CommentListActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_answer_list.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.toast

@Route(path = QA_ANSWER_LIST)
class AnswerListActivity : BaseActivity() {
    companion object {
        @JvmField
        val TAG: String = AnswerListActivity::class.java.simpleName

        const val PARAM_QUESTION = "question"
        const val REQUEST_REFRESH_LIST = 0x2

        fun activityStart(fragment: Fragment, question: Question, requestCode: Int) {
            if (!BaseApp.isLogin) {
                EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用邮问哦~"))
                return
            }
            fragment.startActivityForResult<AnswerListActivity>(requestCode, PARAM_QUESTION to question)
        }
    }

    private lateinit var viewModel: AnswerListViewModel
    private var progressDialog: ProgressDialog? = null
    private fun initProgressBar() = indeterminateProgressDialog(message = "Loading...") {
        setOnDismissListener { viewModel.onProgressDialogDismissed() }
    }

    override val isFragmentActivity = false

    private lateinit var headerAdapter: AnswerListHeaderAdapter
    private lateinit var answerListAdapter: AnswerListAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer_list)
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null) {
            initViewModel(intent.getParcelableExtra(PARAM_QUESTION))
            initView(intent.getParcelableExtra(PARAM_QUESTION))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    private fun initViewModel(question: Question) {
        viewModel = ViewModelProviders.of(this, AnswerListViewModel.Factory(question)).get(AnswerListViewModel::class.java)
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
    }

    private fun initView(question: Question) {
        initRv()
        //设置标题
        qa_tv_toolbar_title.text = question.title
        qa_ib_toolbar_back.setOnClickListener { finish() }
        observeListChangeEvent()
        if (question.isSelf) {
            //不展示更多功能
            qa_ib_toolbar_more.gone()
        } else {
            qa_ib_toolbar_more.setOnClickListener {
                ReportDialog(this@AnswerListActivity).apply {
                    pressReport = {
                        viewModel.report(it)
                    }
                    viewModel.backPreActivityEvent.observeNotNull {
                        dismiss()
                    }
                    viewModel.backPreActivityIgnoreEvent.observeNotNull{
                        dismiss()
                        finish()
                    }
                    pressQuestionIgnore={
                        viewModel.ignoreQuestion()
                    }
                }.show()
            }
        }
    }

    private fun initRv() {
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidate()
        }
        headerAdapter = AnswerListHeaderAdapter { answerListAdapter.resortList(it) }
        answerListAdapter = AnswerListAdapter(this).apply {
            onItemClickListener = { _, answer ->
                CommentListActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value!!, answer)
            }
            onPriseClickListener = { i: Int, answer: Answer ->
                viewModel.clickPraiseButton(answer)
                viewModel.apply {
                    refreshPreActivityEvent.observeNotNull {
                        answerListAdapter.notifyItemChanged(i)
                    }
                }
            }
        }
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_answer_list_no_answer_hint))
        val adapterWrapper = RvAdapterWrapper(answerListAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_answer_list.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@AnswerListActivity)
            adapter = adapterWrapper
        }
    }

    private inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@AnswerListActivity, Observer { onChange(it) })

    private inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@AnswerListActivity, Observer {
        it ?: return@Observer
        onChange(it)
    })

    private fun observeListChangeEvent() = viewModel.apply {
        getMyReward()

        questionLiveData.observeNotNull {
            headerAdapter.refreshData(listOf(it))
            answerListAdapter.setQuestionInfo(it)
        }

        bottomViewEvent.observe {
            when {
                it == null -> fl_answer.gone()
                it -> switchToQuestioner()
                else -> switchToHelper()
            }
        }

        observeAnswerList(Observer {
            if (it != null) {
                answerListAdapter.refreshData(it)
            }
        })

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

        backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                val data = Intent()
                data.putExtra("type", questionLiveData.value!!.kind)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    private fun switchToQuestioner() {
        fl_answer.gone()
    }

    private fun switchToHelper() {
        fl_answer.visibility
        fl_answer.setOnClickListener {
            AnswerActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value?.id
                    ?: "", viewModel.questionLiveData.value?.description
                    ?: "", viewModel.questionLiveData.value?.photoUrl
                    ?: listOf(), REQUEST_REFRESH_LIST)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_REFRESH_LIST -> {
                    viewModel.invalidate()
                    val question = viewModel.questionLiveData.value!!
                    question.answerNum++
                    headerAdapter.refreshData(listOf(question))
                }
                CommentListActivity.REQUEST_CODE -> {
                    viewModel.invalidate()
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun adoptAnswer(event: AdoptAnswerEvent) {
        viewModel.adoptAnswer(event.aId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun openShareQuestion(event: OpenShareQuestionEvent) {
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null) {
            EventBus.getDefault().removeStickyEvent(event)
        } else {
            var questionWrapper: RedrockApiWrapper<Question>? = null

            try {
                questionWrapper = Gson().fromJson<RedrockApiWrapper<Question>>(event.questionJson,
                        object : TypeToken<RedrockApiWrapper<Question>>() {}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (questionWrapper == null) {
                toast("无法识别问题,可能问题已过期")
            } else if (!questionWrapper.isSuccessful) {
                toast(questionWrapper.info ?: "未知错误")
            } else {
                initViewModel(questionWrapper.data)
                initView(questionWrapper.data)
            }
        }
    }
}