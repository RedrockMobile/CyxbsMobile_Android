package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity.Companion.RESULT_CODE
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleSquareAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleSquareViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_square.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import java.util.ArrayList

class CircleSquareActivity : BaseViewModelActivity<CircleSquareViewModel>() {
    var adapter: CircleSquareAdapter? = null
    var mPosition=0//记录当前item的位置
    var topicList=ArrayList<Topic>()
    override val isFragmentActivity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_square)
        initToolbar()
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RESULT_CODE->if (resultCode== Activity.RESULT_OK){
                val resultTopic=data?.getParcelableExtra<Topic>("topic_return")
                LogUtils.d("zt","4")
                LogUtils.d("zt","接收到的top"+resultTopic)
                if (resultTopic != null) {
                    topicList.set(mPosition,resultTopic)
                    LogUtils.d("zt","toplist中对应位置的topic"+topicList.toString())
                    topicList.let { adapter?.refreshData(it) }
                }
            }
        }
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnClickListener(View.OnClickListener {
            finish()
            return@OnClickListener
        })
        qa_tv_toolbar_title.text = "圈子广场"
    }

    private fun initView() {
        adapter = CircleSquareAdapter(viewModel){topic, view,position->
            mPosition=position
            CircleDetailActivity.activityStart(this,view,topic)
        }
        rv_circle_square.layoutManager = LinearLayoutManager(context)
        rv_circle_square.adapter = adapter
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe {
            if (it != null) {
                LogUtils.d("zt","2")
                adapter?.refreshData(it)
                topicList.addAll(it)
            }
        }
    }

}