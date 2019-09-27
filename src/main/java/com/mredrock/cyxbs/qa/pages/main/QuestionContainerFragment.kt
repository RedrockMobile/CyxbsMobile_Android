package com.mredrock.cyxbs.qa.pages.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import kotlinx.android.synthetic.main.qa_dialog_quiz_type_select.view.*
import kotlinx.android.synthetic.main.qa_fragment_question_container.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/22.
 */
@Route(path = QA_ENTRY)
class QuestionContainerFragment : BaseFragment(), View.OnClickListener {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1
    }

    private val titles = listOf(Question.ALL, Question.STUDY, Question.LIFE, Question.EMOTION, Question.OTHER)
    private val quizTypeSelectDialog: BottomSheetDialog by lazy { createTypeSelectDialog() }
    private lateinit var childFragments: List<QuestionListFragment>

    private lateinit var curSelectorItem: AppCompatCheckedTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.qa_fragment_question_container, container, false)
        childFragments = titles.map { QuestionListFragment().apply { title = it } }
        root.vp_question.adapter = QAViewPagerAdapter(childFragments, childFragmentManager)
        //预加载所有部分保证提问后所有fragment能够被通知刷新，同时保证退出账号时只加载一次对话框
        root.vp_question.offscreenPageLimit = if (BaseApp.isLogin) 5 else 0
        root.tl_category.setupWithViewPager(root.vp_question)
        root.fab_quiz.setOnClickListener {
            if (BaseApp.isLogin) {
                quizTypeSelectDialog.show()
            } else {
                EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用掌邮哦~"))
            }
        }
        return root
    }

    private fun createTypeSelectDialog(): BottomSheetDialog {
        val contentView = layoutInflater.inflate(R.layout.qa_dialog_quiz_type_select, null, false)
        val bottomSheetDialog = BottomSheetDialog(context!!).apply {
            setContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        contentView.apply {
            tv_quiz_dialog_type_study.setOnClickListener(this@QuestionContainerFragment)
            tv_quiz_dialog_type_life.setOnClickListener(this@QuestionContainerFragment)
            tv_quiz_dialog_type_emotion.setOnClickListener(this@QuestionContainerFragment)
            tv_quiz_dialog_type_other.setOnClickListener(this@QuestionContainerFragment)
            iv_quiz_dialog_exit.setOnClickListener { quizTypeSelectDialog.dismiss() }
            tv_quiz_dialog_type_next.setOnClickListener {
                quizTypeSelectDialog.dismiss()
                QuizActivity.activityStart(this@QuestionContainerFragment, curSelectorItem.text.toString(), REQUEST_LIST_REFRESH_ACTIVITY)
            }
        }
        curSelectorItem = contentView.tv_quiz_dialog_type_study
        return bottomSheetDialog
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
}