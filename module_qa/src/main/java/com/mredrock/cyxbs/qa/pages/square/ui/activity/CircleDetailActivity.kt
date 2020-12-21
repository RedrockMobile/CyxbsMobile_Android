package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.*

class CircleDetailActivity : BaseViewModelActivity<CircleDetailViewModel>() {
    companion object {
        val RESULT_CODE=1
        fun activityStart(activity: BaseActivity, topicItemView: View, data: Topic) {
            activity.let {
                val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, topicItemView, "topicItem")
                val intent = Intent(context, CircleDetailActivity::class.java)
                intent.putExtra("topicItem", data)
                it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                startActivityForResult(activity,intent, RESULT_CODE,opt.toBundle())
            }
        }
    }
    override val isFragmentActivity = true
    lateinit var topic: Topic
    private val lastNewFragment by lazy(LazyThreadSafetyMode.NONE) {
        LastNewFragment()
    }
    private val hotFragment by lazy(LazyThreadSafetyMode.NONE) {
        HotFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        qa_vp_circle_detail.adapter = NewHotViewPagerAdapter(this, listOf(lastNewFragment, hotFragment))
        initTab()
        initView()
        initClick()
    }
    override fun onBackPressed() {
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        val intent=Intent()
        intent.putExtra("topic_return",topic)
        setResult(Activity.RESULT_OK,intent)
        LogUtils.d("zt","3")
        LogUtils.d("zt","返回的top"+topic)
        finish()
        super.onBackPressed()
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initClick() {
        qa_circle_detail_iv_back.setOnSingleClickListener {
            val intent=Intent()
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        btn_circle_square_concern.setOnClickListener {
            if (topic._isFollow.equals(1)){
                //关注的状态下点击，取消关注
                viewModel.followTopic(topic.topicName,topic._isFollow.equals(1))
                btn_circle_square_concern.background=context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
                topic._isFollow=0
            }else{
                viewModel.followTopic(topic.topicName,topic._isFollow.equals(1))
                btn_circle_square_concern.background=context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
                topic._isFollow=1
            }
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun initView() {
        topic=intent.getParcelableExtra("topicItem")
        LogUtils.d("zt",topic.toString())
        tv_circle_square_name.text = topic.topicName
        tv_circle_square_descriprion.text = topic.introduction
        tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
        btn_circle_square_concern.text = "+关注"
        if (topic._isFollow.equals(1)){
            btn_circle_square_concern.background=context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
        }else{
            btn_circle_square_concern.background=context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
        }
    }

    private fun initTab() {
        TabLayoutMediator(qa_circle_detail_tab, qa_vp_circle_detail) { tab, position ->
            when (position) {
                0 -> tab.text = "最新"
                1 -> tab.text = "热门"
            }
        }.attach()
    }
}