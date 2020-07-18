package com.mredrock.cyxbs.qa.pages.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.CurrentDateInformationEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import kotlinx.android.synthetic.main.qa_fragment_question_container.*
import kotlinx.android.synthetic.main.qa_fragment_question_container.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created By jay68 on 2018/8/22.
 */
@Route(path = QA_ENTRY)
class QuestionContainerFragment : BaseFragment(), View.OnClickListener, EventBusLifecycleSubscriber {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1
    }

    private val titles = listOf(Question.NEW, Question.STUDY, Question.ANONYMOUS, Question.LIFE, Question.OTHER)
    private lateinit var childFragments: List<QuestionListFragment>

    private lateinit var curSelectorItem: AppCompatCheckedTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_question_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragments = titles.map { QuestionListFragment.newInstance(it) }
        view.vp_question.adapter = QAViewPagerAdapter(childFragments, childFragmentManager)
        //预加载所有部分保证提问后所有fragment能够被通知刷新，同时保证退出账号时只加载一次对话框
        view.vp_question.offscreenPageLimit = 5
        view.tl_category.apply {
            setupWithViewPager(view.vp_question)
            setSelectedTabIndicator(R.drawable.qa_question_tab_indicator)
        }
        btn_ask_question.setOnClickListener {
            QuizActivity.activityStart(this@QuestionContainerFragment, "学习", REQUEST_LIST_REFRESH_ACTIVITY)
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btn_ask_question.pressToZoomOut()
    }

    override fun onClick(v: View) {
        if (v == curSelectorItem) {
            return
        }
        curSelectorItem.isChecked = false
        curSelectorItem = v as AppCompatCheckedTextView
        curSelectorItem.isChecked = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun setCurrentDate(event: CurrentDateInformationEvent) {
        tv_current_time.text = event.time
    }
}