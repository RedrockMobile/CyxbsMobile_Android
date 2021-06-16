package com.mredrock.cyxbs.qa.pages.square.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.QA_CIRCLE_DETAIL
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RESULT_CODE
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.NewHotViewPagerAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.HotFragment
import com.mredrock.cyxbs.qa.pages.square.ui.fragment.LastNewFragment
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import com.mredrock.cyxbs.qa.ui.widget.ShareDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_activity_circle_detail.*
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.*

@Route(path = QA_CIRCLE_DETAIL)
class CircleDetailActivity : BaseViewModelActivity<CircleDetailViewModel>() {

    companion object {
        private var startPosition = 0
        fun activityStartFromSquare(activity: BaseActivity, topicItemView: View, data: Topic) {
            activity.let {
                val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    it,
                    topicItemView,
                    "topicItem"
                )
                val intent = Intent(context, CircleDetailActivity::class.java)
                intent.putExtra("topicItem", data)
                it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                startPosition = RESULT_CODE
                startActivityForResult(activity, intent, RESULT_CODE, opt.toBundle())
            }
        }

        fun activityStartFromCircle(fragment: Fragment, topicItemView: View, data: Topic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it,
                        topicItemView,
                        "topicItem"
                    )
                    val intent = Intent(BaseApp.context, CircleDetailActivity::class.java)
                    intent.putExtra("topicItem", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startPosition = DYNAMIC_DETAIL_REQUEST
                    startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST, opt.toBundle())
                }
            }
        }

        //自发送动态的页面而来，这里等同于自前端分享跳转而来
        fun activityStartFormQuiz(activity: Activity, circleId: String) {
            val intent = Intent(activity, CircleDetailActivity::class.java)
            intent.putExtra("id", circleId)
            intent.putExtra("isFromReceive", true)
            activity.startActivity(intent)
        }
    }

    private var mTencent: Tencent? = null

    private var isFormReceive = false

    override fun getViewModelFactory(): ViewModelProvider.Factory? {
        var loop = 1
        intent.extras?.apply {
            loop = getInt("id")
        }
        return CircleDetailViewModel.Factory("main", loop)
    }

    lateinit var topic: Topic

    private val lastNewFragment by lazy(LazyThreadSafetyMode.NONE) {
        LastNewFragment().apply {
            arguments = Bundle().apply {
                putInt("loop", viewModel.topicId)
                putString("type", "latest")
            }
        }
    }
    private val hotFragment by lazy(LazyThreadSafetyMode.NONE) {
        HotFragment().apply {
            arguments = Bundle().apply {
                putInt("loop", viewModel.topicId)
                putString("type", "hot")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_circle_detail)
        intent.extras?.apply { //在目前情况下，属于是自receive跳转的情况，所以需要再度请求圈子信息
            val id = try {
                getString("id")?.toInt() ?: 0
            } catch (e: Exception) {
                return@apply
            }
            if (id <= 0) {
                return@apply
            }
            isFormReceive = getBoolean("isFromReceive")
            if (!isFormReceive) return@apply
            //下面的代码只会在前端跳转时执行
            ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getTopicGround("问答圈", "test1")
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                    onNext = {
                        if (id !in 1..it.size) {
                            return@safeSubscribeBy
                        }
                        topic = it[id - 1]
                        viewModel.topicId = topic.topicId.toInt()
                        iv_circle_square.setAvatarImageFromUrl(topic.topicLogo)
                        tv_circle_square_name.text = topic.topicName
                        tv_circle_square_descriprion.text = topic.introduction
                        tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
                        qa_detail_tv_title.text = topic.topicName
                        if (topic._isFollow == 1) {
                            btn_circle_square_concern.text = "已关注"
                            btn_circle_square_concern.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.qa_shape_send_dynamic_btn_grey_background
                            )
                        } else {
                            btn_circle_square_concern.text = "+关注"
                            btn_circle_square_concern.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.qa_shape_send_dynamic_btn_blue_background
                            )
                        }
                        qa_vp_circle_detail.adapter = NewHotViewPagerAdapter(
                            this@CircleDetailActivity,
                            listOf(lastNewFragment, hotFragment)
                        )
                        initTab()
                        initClick()
                        initObserve()
                    },
                    onError = {
                        context.toast("获取圈子信息失败")
                    }
                )
        }
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        initView()
        if (!isFormReceive) {
            qa_vp_circle_detail.adapter =
                NewHotViewPagerAdapter(this, listOf(lastNewFragment, hotFragment))
            initTab()
            initClick()
            initObserve()
        }
    }

    override fun onBackPressed() {
        if (!isFormReceive) {
            LogUtils.d("zt", topic.toString())
            when (startPosition) {
                RESULT_CODE -> {
                    val intent = Intent()
                    intent.putExtra("topic_return", topic)
                    setResult(Activity.RESULT_OK, intent)
                }
                DYNAMIC_DETAIL_REQUEST -> {
                    setResult(NEED_REFRESH_RESULT)
                }
            }
        }
        finish()
        super.onBackPressed()
    }

    private fun initObserve() {
        viewModel.followStateChangedMarkObservableByActivity.observe {
            changeFollowState()
        }
    }

    private fun initClick() {
        qa_circle_detail_iv_back.setOnSingleClickListener {
            when (startPosition) {
                RESULT_CODE -> {
                    val intent = Intent()
                    intent.putExtra("topic_return", topic)
                    setResult(Activity.RESULT_OK, intent)
                }
                DYNAMIC_DETAIL_REQUEST -> {
                    setResult(NEED_REFRESH_RESULT)
                }
            }
            finish()
        }
        btn_circle_square_concern.setOnClickListener {
            changeFollowState()
            viewModel.followStateChangedMarkObservableByFragment.value = true
        }

        qa_iv_circle_detail_share.setOnSingleClickListener {
            val url = "${CommentConfig.SHARE_URL}quanzi?id=${topic.topicId}"
            ShareDialog(it.context).apply {
                initView(onCancelListener = View.OnClickListener {
                    dismiss()
                }, qqshare = View.OnClickListener {
                    mTencent?.let { it1 ->
                        ShareUtils.qqShare(
                            it1,
                            this@CircleDetailActivity,
                            topic.topicName,
                            topic.introduction,
                            url,
                            topic.topicLogo
                        )
                    }
                }, qqZoneShare = View.OnClickListener {
                    val topicArray = ArrayList<String>()//写出这样的代码，我很抱歉，可惜腾讯要的是ArrayList
                    topicArray.add(topic.topicLogo)
                    mTencent?.let { it1 ->
                        ShareUtils.qqQzoneShare(
                            it1,
                            this@CircleDetailActivity,
                            topic.topicName,
                            topic.introduction,
                            url,
                            topicArray
                        )
                    }
                }, weChatShare = View.OnClickListener {
                    CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT)
                        .show()

                }, friendShipCircle = View.OnClickListener {
                    CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT)
                        .show()

                }, copylink = View.OnClickListener {
                    ClipboardController.copyText(this@CircleDetailActivity, url)
                })
            }.show()
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun initView() {
        if (!isFormReceive) {
            topic = intent.getParcelableExtra("topicItem")
            viewModel.topicId = topic.topicId.toInt()
            iv_circle_square.setAvatarImageFromUrl(topic.topicLogo)
            tv_circle_square_name.text = topic.topicName
            tv_circle_square_descriprion.text = topic.introduction
            tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
            qa_detail_tv_title.text = topic.topicName
            if (topic._isFollow.equals(1)) {
                btn_circle_square_concern.text = "已关注"
                btn_circle_square_concern.background =
                    context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
            } else {
                btn_circle_square_concern.text = "+关注"
                btn_circle_square_concern.background =
                    context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == NEED_REFRESH_RESULT) {
            qa_vp_circle_detail.adapter = NewHotViewPagerAdapter(
                this@CircleDetailActivity,
                listOf(lastNewFragment, hotFragment)
            )
        }
    }

    private fun changeFollowState() {
        if (topic._isFollow == 1) {
            //关注的状态下点击，取消关注
            viewModel.followTopic(topic.topicName, topic._isFollow == 1)
            btn_circle_square_concern.background = ContextCompat.getDrawable(
                this,
                R.drawable.qa_shape_send_dynamic_btn_blue_background
            )
            btn_circle_square_concern.text = "+关注"
            topic.follow_count = topic.follow_count - 1
            tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
            topic._isFollow = 0
        } else {
            viewModel.followTopic(topic.topicName, topic._isFollow == 1)
            btn_circle_square_concern.background = ContextCompat.getDrawable(
                this,
                R.drawable.qa_shape_send_dynamic_btn_grey_background
            )
            btn_circle_square_concern.text = "已关注"
            topic.follow_count = topic.follow_count + 1
            tv_circle_square_person_number.text = topic.follow_count.toString() + "个成员"
            topic._isFollow = 1
        }
    }
}