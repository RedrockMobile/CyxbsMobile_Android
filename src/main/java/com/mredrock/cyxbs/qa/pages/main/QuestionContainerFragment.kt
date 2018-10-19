package com.mredrock.cyxbs.qa.pages.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment
import com.mredrock.cyxbs.qa.pages.quiz.QuizViewModel
import kotlinx.android.synthetic.main.qa_fragment_question_container.view.*

/**
 * Created By jay68 on 2018/8/22.
 */
@Route(path = QA_ENTRY)
class QuestionContainerFragment : BaseViewModelFragment<QuizViewModel>() {
    override val viewModelClass = QuizViewModel::class.java

    private val titles = listOf(Question.ALL, Question.STUDY, Question.EMOTION, Question.OTHER)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.qa_fragment_question_container, container, false)
        val fragments = titles.map { QuestionListFragment().apply { title = it } }
        root.vp_question.adapter = QAViewPagerAdapter(fragments, activity!!.supportFragmentManager)
        root.tl_category.setupWithViewPager(root.vp_question)
        root.fab_quiz.setOnClickListener { viewModel.quiz() }
        return root
    }
}