package com.mredrock.cyxbs.qa.pages.answer.ui

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.OpenShareQuestionEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerListViewModel
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.pages.comment.ui.CommentListActivity
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
import com.mredrock.cyxbs.qa.pages.report.ui.ReportOrSharePopupWindow
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_answer_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.toast
import java.lang.Exception

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

        common_toolbar.init(getString(R.string.qa_answer_list_title))
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null) {
            initViewModel(intent.getParcelableExtra(PARAM_QUESTION))
            initView()
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

    private fun initView() {
        initRv()
        observeListChangeEvent()
    }

    private fun initRv() {
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidate() }
        headerAdapter = AnswerListHeaderAdapter { answerListAdapter.resortList(it) }
        answerListAdapter = AnswerListAdapter(this).apply {
            onItemClickListener = { _, answer ->
                CommentListActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value!!, answer)
            }
        }
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_answer_list_no_answer_hint))
        val adapterWrapper = RvAdapterWrapper(answerListAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_answer_list.apply {
            layoutManager = LinearLayoutManager(this@AnswerListActivity)
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
                it == null -> card_bottom_container.gone()
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

    private val rewardSetDialog by lazy {
        RewardSetDialog(this@AnswerListActivity, viewModel.myRewardCount).apply {
            onSubmitButtonClickListener = {
                if (viewModel.addReward(it)) {
                    dismiss()
                }
            }
        }
    }

    private fun switchToQuestioner() {
        card_bottom_container.visible()
        val leftDrawable = resources.getDrawable(R.drawable.qa_ic_answer_list_add_reward)
        tv_left.text = getString(R.string.qa_answer_list_add_reward)
        tv_left.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null)
        fl_left.setOnClickListener {
            rewardSetDialog.show()
        }

        val rightDrawable = resources.getDrawable(R.drawable.qa_ic_answer_list_cancel)
        tv_right.text = getString(R.string.qa_answer_list_cancel_question)
        tv_right.setCompoundDrawablesRelativeWithIntrinsicBounds(rightDrawable, null, null, null)
        fl_right.setOnClickListener { viewModel.cancelQuestion() }
    }

    private fun switchToHelper() {
        card_bottom_container.visible()
        val leftDrawable = resources.getDrawable(R.drawable.qa_ic_answer_list_ignore)
        tv_left.text = getString(R.string.qa_answer_list_ignore_question)
        tv_left.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null)
        fl_left.setOnClickListener { viewModel.ignoreQuestion() }

        val rightDrawable = resources.getDrawable(R.drawable.qa_ic_answer_list_help)
        tv_right.text = getString(R.string.qa_answer_list_help)
        tv_right.setCompoundDrawablesRelativeWithIntrinsicBounds(rightDrawable, null, null, null)
        fl_right.setOnClickListener { AnswerActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value!!.id, REQUEST_REFRESH_LIST) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AnswerListActivity.REQUEST_REFRESH_LIST -> {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qa_report_and_share_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.more -> {
                ReportOrSharePopupWindow(this, viewModel.questionLiveData.value ?: return false, common_toolbar, card_frame).show()
                true
            }
            else -> false
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
            }else if (!questionWrapper.isSuccessful) {
                toast(questionWrapper.info ?: "未知错误")
            } else {
                initViewModel(questionWrapper.data)
                initView()
            }
        }
    }
}