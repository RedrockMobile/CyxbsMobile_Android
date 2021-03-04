package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.startActivityForResult
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RESULT_CODE
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleSquareAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleSquareViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_square.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import java.util.ArrayList

class CircleSquareActivity : BaseViewModelActivity<CircleSquareViewModel>() {
    var adapter: CircleSquareAdapter? = null
    var mPosition = 0//记录当前item的位置
    var topicList = ArrayList<Topic>()
//    override val isFragmentActivity = false

    companion object {
        fun activityStartFromDynamic(fragment: Fragment) {
            fragment.apply {
                activity?.let {
                    val intent=Intent(BaseApp.context, CircleSquareActivity::class.java)
                    startActivityForResult(intent,RequestResultCode.DYNAMIC_DETAIL_REQUEST)
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
                val resultTopic = data?.getParcelableExtra<Topic>("topic_return")
                if (resultTopic != null) {
                    topicList[mPosition] = resultTopic
                    topicList.let { adapter?.refreshData(it) }
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(NEED_REFRESH_RESULT)
        finish()
        super.onBackPressed()
    }

    private fun initToolbar() {
        qa_ib_toolbar_back.setOnSingleClickListener {
            setResult(NEED_REFRESH_RESULT)
            finish()
        }
        qa_tv_toolbar_title.text = "圈子广场"
    }

    private fun initView() {
        adapter = CircleSquareAdapter(viewModel) { topic, view, position ->
            mPosition = position
            CircleDetailActivity.activityStartFromSquare(this, view, topic)
        }
        rv_circle_square.layoutManager = LinearLayoutManager(context)
        rv_circle_square.adapter = adapter
        viewModel.getAllCirCleData("问答圈", "test1")
        viewModel.allCircle.observe {
            if (it != null) {
                adapter?.refreshData(it)
                topicList.addAll(it)
            }
        }
    }

}