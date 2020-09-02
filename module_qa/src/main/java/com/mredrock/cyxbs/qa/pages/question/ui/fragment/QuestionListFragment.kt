package com.mredrock.cyxbs.qa.pages.question.ui.fragment


import android.os.Bundle
import com.mredrock.cyxbs.qa.pages.question.viewmodel.QuestionListViewModel

/**
 * Created By jay68 on 2018/8/22.
 */
class QuestionListFragment : BaseQuestionListFragment<QuestionListViewModel>() {
    companion object {
        fun newInstance(title: String): QuestionListFragment = QuestionListFragment().apply {
            arguments = Bundle().apply { putString(FRAGMENT_TITLE, title) }
        }
    }

    override val viewModelClass: Class<QuestionListViewModel> = QuestionListViewModel::class.java
    override fun getViewModelFactory() = QuestionListViewModel.Factory(title)
}