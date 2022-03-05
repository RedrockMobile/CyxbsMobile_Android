package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RESULT_CODE
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleSquareAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleSquareViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_square.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import java.util.*

class CircleSquareActivity : BaseViewModelActivity<CircleSquareViewModel>() {

    private lateinit var adapter: CircleSquareAdapter

    /**
     * 记录点击的Topic，如果返回时数据不一样再次进行网络请求刷新数据
     */
    private var clickTopic: Topic? = null

    companion object {
        fun activityStartFromDynamic(fragment: Fragment) {
            fragment.apply {
                activity?.let {
                    val intent = Intent(it, CircleSquareActivity::class.java)
                    startActivityForResult(intent, RequestResultCode.DYNAMIC_DETAIL_REQUEST)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_square)
        initToolbar()
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_CODE -> if (resultCode == Activity.RESULT_OK) {
                // 返回的最新数据
                val resultTopic = data?.getParcelableExtra<Topic>("topic_return")
                if (resultTopic != null && clickTopic != null) {
                    if (resultTopic != clickTopic){
                        // 返回时数据发生改变，重新请求数据
                        viewModel.getAllCirCleData("问答圈", "test1")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(NEED_REFRESH_RESULT)
        super.onBackPressed()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initToolbar() {
        qa_ib_toolbar_back.setOnSingleClickListener {
            setResult(NEED_REFRESH_RESULT)
            onBackPressed()
        }
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_square_title)
        qa_circle_square_toolbar.background =
            ContextCompat.getDrawable(this, R.color.qa_circle_toolbar_back_color)
    }

    private fun initView() {
        adapter = CircleSquareAdapter().apply {
            // 设置item点击事件
            itemClick = { topic, _ ->
                clickTopic = topic
                CircleDetailActivity
                    .activityStartFromSquare(this@CircleSquareActivity,topic)
            }
            // 设置关注按钮点击事件
            concernClick = { topicName, state ->
                // 发送关注圈子的POST请求
                viewModel.followTopic(topicName, state)
            }
        }
        rv_circle_square.layoutManager = LinearLayoutManager(this)
        rv_circle_square.adapter = adapter
        val divide = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.qa_shape_divide_line)?.let {
            divide.setDrawable(it)
            rv_circle_square.addItemDecoration(divide)
        }
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

}