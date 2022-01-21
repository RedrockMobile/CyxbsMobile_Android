package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.CyxbsMob
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CirclesAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicPagerAdapter
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.SearchActivity
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleSquareActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.qa_fragment_dynamic.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @Author: xgl
 * @ClassName: DynamicFragment
 * @Description:
 * @Date: 2020/11/16 22:07
 */
@Route(path = QA_ENTRY)
class DynamicFragment : BaseDynamicFragment(), EventBusLifecycleSubscriber {

    private lateinit var circlesAdapter: CirclesAdapter

    private val recommendFragment = RecommendDynamicFragment()
    private val focusFragment = FocusDynamicFragment()

    override fun initData() {
        initObserve()
        initCircles()
        initScrollText()
        initClick()
        initPager()
    }

    override fun getLayoutId() = R.layout.qa_fragment_dynamic

    private fun initObserve() {

        viewModel.myCircle.observe(viewLifecycleOwner, Observer {
            circlesAdapter.addCircleData(it)
        })

        viewModel.topicMessageList.observe(viewLifecycleOwner, Observer {
            circlesAdapter.addTopicMessageData(it)
        })
    }

    private fun initCircles() {
        viewModel.getAllCirCleData("问答圈", "test1")

        circlesAdapter = CirclesAdapter({ topic, view ->
            if (topic.topicId == "0") {
                CircleSquareActivity.activityStartFromDynamic(this)
            } else {
                CircleDetailActivity.activityStartFromCircle(this, view, topic)
            }
        }, this)

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        qa_rv_circles_List.apply {
            layoutManager = linearLayoutManager
            adapter = circlesAdapter
        }

        //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
        refreshTopicMessage()
        viewModel.getMyCirCleData()
    }

    private fun refreshTopicMessage() {
        //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
        if (!TopicDataSet.getOutCirCleDetailTime().isNullOrEmpty()) {
            TopicDataSet.getOutCirCleDetailTime()?.let { viewModel.getTopicMessages(it) }
        }
    }

    private fun initScrollText() {
        //搜索滚动词
        viewModel.getScrollerText()
        val loaderView = getTextView("")
        vf_hot_search.addView(loaderView)
        initHotSearch(vf_hot_search, rl_qa_hot_search)
    }

    private fun initHotSearch(viewFlipper: ViewFlipper, relativeLayout: RelativeLayout) {
        viewModel.hotWords.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                viewFlipper.removeAllViews()
                for (i in it) {
                    viewFlipper.addView(getTextView(context?.getString(R.string.qa_search_hot_word_key) + i))
                }
            }
        })

        vf_hot_search.startFlipping()
        relativeLayout.setOnSingleClickListener {
            val hotWord = StringBuilder()
            vf_hot_search.run {
                val allHotWord = (getChildAt(displayedChild) as TextView).text.toString()
                if (allHotWord.length > HOT_WORD_HEAD_LENGTH) {
                    for (i in HOT_WORD_HEAD_LENGTH until allHotWord.length) {
                        hotWord.append(allHotWord[i])
                    }
                }
            }

            //跳转到searchActivity时让这个window的alpha为透明,防止searchActivity的window alpha变小时这个页面的window展示出来
            //在fragment onResume时将alpha值设置回来
            handler.postDelayed(windowAlphaRunnable, 1000)

            SearchActivity.activityStart(this, hotWord.toString())
            MobclickAgent.onEvent(context, CyxbsMob.Event.QA_SEARCH_RECOMMEND)
        }

        viewFlipper.setFlipInterval(6555)
        viewFlipper.setInAnimation(context, R.anim.qa_anim_hot_search_flip_in)
        viewFlipper.setOutAnimation(context, R.anim.qa_anim_hot_search_flip_out)
    }

    private fun getTextView(info: String): TextView {
        return TextView(context).apply {
            text = info
            maxLines = 1
            maxEms = 15
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            alpha = 0.51f
            setTextColor(ContextCompat.getColor(context, R.color.common_level_two_font_color))
            textSize = 14f
        }
    }

    private fun initClick() {
        qa_bt_to_quiz.setOnSingleClickListener {
            turnToQuiz()
        }
    }

    private fun turnToQuiz() {
        context?.doIfLogin("提问") {

            QuizActivity.activityStart(this, "发动态", REQUEST_LIST_REFRESH_ACTIVITY)
            MobclickAgent.onEvent(context, CyxbsMob.Event.CLICK_ASK)

        }
    }

    private fun initPager() {
        qa_vp_dynamic.adapter = DynamicPagerAdapter(requireActivity()).apply {
            addFragment(recommendFragment)
            addFragment(focusFragment)
        }

        TabLayoutMediator(qa_tl_dynamic, qa_vp_dynamic) { tab, position ->
            when (position) {
                0 -> tab.text = "推荐"
                1 -> tab.text = "关注"
            }
        }.attach()

    }

    private fun invalidateDynamicList() {
        viewModel.invalidateRecommendList()
        viewModel.invalidateFocusList()
    }

    override fun onResume() {
        super.onResume()
        vf_hot_search.startFlipping()
    }

    override fun onPause() {
        super.onPause()
        vf_hot_search.stopFlipping()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == NEED_REFRESH_RESULT) {
            // 需要刷新则刷新显示动态
            viewModel.getAllCirCleData("问答圈", "test1")
            //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
            viewModel.getMyCirCleData()
            refreshTopicMessage()
            invalidateDynamicList()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun refreshQuestionList(event: RefreshQaEvent) {
    }
//        if (isRvAtTop) {
//            viewModel.apply {
//                invalidateFocusList()
//                invalidateRecommendList()
//            }
//        }
//        else {
//            focusFragment.qa_rv_dynamic_List_focus.smoothScrollToPosition(0)
//            recommendFragment.qa_rv_dynamic_List_recommend.smoothScrollToPosition(0)
//
//        }
//    }
}