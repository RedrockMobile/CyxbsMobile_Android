package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity
import kotlinx.android.synthetic.main.qa_fragment_search_no_result.*

/**
 *@Date 2020-11-26
 *@Time 21:14
 *@author SpreadWater
 *@description
 */
class QuestionSearchedNoResultFragment :BaseViewModelFragment<QuestionSearchedViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_search_no_result,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qa_btn_no_result_ask_question.setOnSingleClickListener {
            //TODO 页面跳转到提问

        }
    }
    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        BaseApp.context.startActivity(intent)
    }
}