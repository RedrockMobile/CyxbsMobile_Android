package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleSquareAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleSquareViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_square.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*

class CircleSquareActivity : BaseViewModelActivity<CircleSquareViewModel>() {
    var adapter: CircleSquareAdapter? = null

    override val isFragmentActivity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_square)
        initToolbar()
        initView()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            finish()
            return@OnClickListener
        })
        qa_tv_toolbar_title.text = "圈子广场"
    }

    private fun initView() {
        adapter = CircleSquareAdapter(viewModel){topic, view->
            CircleDetailActivity.activityStart(this,view,topic)
        }
        rv_circle_square.layoutManager = LinearLayoutManager(context)
        rv_circle_square.adapter = adapter
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe {
            if (it != null) {
                adapter?.refreshData(it)
            }
        }
    }

}