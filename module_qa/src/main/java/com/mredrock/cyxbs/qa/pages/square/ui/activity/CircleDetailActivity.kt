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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RESULT_CODE
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.*
import kotlin.properties.Delegates

class CircleDetailActivity : BaseViewModelActivity<CircleDetailViewModel>() {

    companion object {
        private var startPosition=0

        fun activityStartFromSquare(activity: BaseActivity, topicItemView: View, data: Topic) {
            activity.let {
                val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, topicItemView, "topicItem")
                val intent = Intent(context, CircleDetailActivity::class.java)
                intent.putExtra("topicItem", data)
                it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                startPosition= RESULT_CODE
                startActivityForResult(activity, intent, RESULT_CODE, opt.toBundle())
            }
        }

        fun activityStartFromCircle(fragment: Fragment, topicItemView: View, data: Topic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, topicItemView, "topicItem")
                    val intent = Intent(BaseApp.context, CircleDetailActivity::class.java)
                    intent.putExtra("topicItem", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startPosition= DYNAMIC_DETAIL_REQUEST
                    startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST, opt.toBundle())
                }
            }
        }
    }

    override val isFragmentActivity = true

    override fun getViewModelFactory()=CircleDetailViewModel.Factory("main",1)

    lateinit var topic: Topic

    private val lastNewFragment by lazy(LazyThreadSafetyMode.NONE) {
        LastNewFragment(topic.topicId.toInt())
    }
    private val hotFragment by lazy(LazyThreadSafetyMode.NONE) {
        HotFragment(topic.topicId.toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        initView()
        qa_vp_circle_detail.adapter = NewHotViewPagerAdapter(this, listOf(lastNewFragment, hotFragment))
        initTab()
        initClick()
    }

    override fun onBackPressed() {
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        LogUtils.d("zt",topic.toString())
        when(startPosition){
            RESULT_CODE->{
                val intent = Intent()
                intent.putExtra("topic_return", topic)
                setResult(Activity.RESULT_OK, intent)
            }
            DYNAMIC_DETAIL_REQUEST->{
                setResult(NEED_REFRESH_RESULT)
            }
        }
        finish()
        super.onBackPressed()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun initClick() {
        qa_circle_detail_iv_back.setOnSingleClickListener {
            window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
            when(startPosition){
                RESULT_CODE->{
                    val intent = Intent()
                    intent.putExtra("topic_return", topic)
                    setResult(Activity.RESULT_OK, intent)
                }
                DYNAMIC_DETAIL_REQUEST->{
                    setResult(NEED_REFRESH_RESULT)
                }
            }
            finish()
        }
        btn_circle_square_concern.setOnClickListener {
            if (topic._isFollow.equals(1)) {
                //关注的状态下点击，取消关注
                viewModel.followTopic(topic.topicName, topic._isFollow.equals(1))
                btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
                topic.follow_count=topic.follow_count-1
                tv_circle_square_person_number.text = topic.follow_count.toString()+ "个成员"
                topic._isFollow = 0
            } else {
                viewModel.followTopic(topic.topicName, topic._isFollow.equals(1))
                btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
                topic.follow_count=topic.follow_count+1
                tv_circle_square_person_number.text = topic.follow_count.toString()+ "个成员"
                topic._isFollow = 1
            }
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun initView() {
        topic = intent.getParcelableExtra("topicItem")
        iv_circle_square.setAvatarImageFromUrl(topic.topicLogo)
        tv_circle_square_name.text = topic.topicName
        tv_circle_square_descriprion.text = topic.introduction
        tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
        btn_circle_square_concern.text = "+关注"
        qa_detail_tv_title.text = topic.topicName
        if (topic._isFollow.equals(1)) {
            btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
        } else {
            btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
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

    override fun finish() {
        super.finish()
        val timeStamp = System.currentTimeMillis() / 1000
        TopicDataSet.storageTopicTime(timeStamp.toString())
    }
}