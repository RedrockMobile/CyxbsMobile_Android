package com.mredrock.cyxbs.qa.pages.answer.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerListViewModel
import com.mredrock.cyxbs.qa.pages.comment.ui.CommentListActivity
import com.mredrock.cyxbs.qa.pages.report.ui.ReportOrSharePopupWindow
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_answer_list.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.startActivityForResult

class AnswerListActivity : BaseViewModelActivity<AnswerListViewModel>() {
    companion object {
        @JvmField
        val TAG: String = AnswerListActivity::class.java.simpleName

        const val PARAM_QUESTION = "question"
        const val REQUEST_REFRESH_LIST = 0x2

        fun activityStart(fragment: Fragment, question: Question, requestCode: Int) {
            if (!BaseApp.isLogin) {
                EventBus.getDefault().post(AskLoginEvent(fragment.getString(R.string.qa_unlogin_error)))
                return
            }
            fragment.startActivityForResult<AnswerListActivity>(requestCode, PARAM_QUESTION to question)
        }
    }

    override val isFragmentActivity = false
    override val viewModelClass = AnswerListViewModel::class.java

    private lateinit var headerAdapter: AnswerListHeaderAdapter
    private lateinit var answerListAdapter: AnswerListAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer_list)

        common_toolbar.init(getString(R.string.qa_answer_list_title))
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

    override fun getViewModelFactory() = AnswerListViewModel.Factory(intent.getParcelableExtra(PARAM_QUESTION))

    private fun observeListChangeEvent() = viewModel.apply {
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

        answerList.observe { answerListAdapter.submitList(it) }

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
        card_bottom_container.visible()
        val leftDrawable = resources.getDrawable(R.drawable.qa_ic_answer_list_add_reward)
        tv_left.text = getString(R.string.qa_answer_list_add_reward)
        tv_left.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null)
        fl_left.setOnClickListener { viewModel.addReward() }

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
        fl_left.setOnClickListener { viewModel.addReward() }

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

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.more -> {
            ReportOrSharePopupWindow(this, viewModel.qid, common_toolbar, card_frame).show()
            true
        }
        else -> false
    }
}