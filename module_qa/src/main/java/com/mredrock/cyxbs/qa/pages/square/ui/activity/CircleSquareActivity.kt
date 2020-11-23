package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.CircleSquare
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleSquareAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CirecleSquareViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_square.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*

class CircleSquareActivity : BaseViewModelActivity<CirecleSquareViewModel>() {
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
        qa_ib_toolbar_more.gone()
    }
    private fun initView(){

        val list=ArrayList<CircleSquare>()
        for (i in 0..4){
            list.add(CircleSquare("高级学术交流中心",477,"重邮也有猫猫图鉴啦，欢迎大家一起来分享你看见的猫猫~"))
        }
        rv_circle_square.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=CircleSquareAdapter().apply {
                addData(list)
            }
        }
    }
}