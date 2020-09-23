package com.mredrock.cyxbs.qa.pages.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.CyxbsMob
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.CurrentDateInformationEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.pages.main.viewmodel.QuestionContainerViewModel
import com.mredrock.cyxbs.qa.pages.question.ui.fragment.FreshManQuestionListFragment
import com.mredrock.cyxbs.qa.pages.question.ui.fragment.QuestionListFragment
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.SearchActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.qa_fragment_question_container.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.mredrock.cyxbs.common.utils.extensions.setSingleOnClickListener

/**
 * Created By jay68 on 2018/8/22.
 */
@Route(path = QA_ENTRY)
class QuestionContainerFragment : BaseViewModelFragment<QuestionContainerViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1

        //R.string.qa_search_hot_word_key 长度
        const val HOT_WORD_HEAD_LENGTH = 6
    }

    private val titles = listOf(Question.NEW, Question.FRESHMAN, Question.STUDY, Question.ANONYMOUS, Question.LIFE, Question.OTHER)
    private val childFragments by lazy(LazyThreadSafetyMode.NONE) {
        titles.map {
            if (it == Question.FRESHMAN) {
                FreshManQuestionListFragment.newInstance(it)
            } else {
                QuestionListFragment.newInstance(it)
            }
        }
    }

    //判断是否加载过热词，首次加载fragment，会加载一次，设置true，onPause就不会加载
    // onStop设置为false，onPause就会加载

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_question_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVP()
        askQuestionClick()
        initScrollText()
    }

    override fun onResume() {
        super.onResume()
        vf_hot_search.startFlipping()
    }

    private fun initVP() {
        val qaViewPagerAdapter = QAViewPagerAdapter(childFragments, childFragmentManager)
        vp_question.adapter = qaViewPagerAdapter
        //预加载所有部分保证提问后所有fragment能够被通知刷新，同时保证退出账号时只加载一次对话框
        vp_question.apply {
            offscreenPageLimit = titles.size
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {

                    MobclickAgent.onEvent(requireContext(),CyxbsMob.Event.SWITCH_QA_PAGE, mutableMapOf(
                            Pair(CyxbsMob.Key.QA_PAGE ,qaViewPagerAdapter.getPageTitle(position))
                    ))
                }

            })
        }
        tl_category.apply {
            setupWithViewPager(vp_question)
            setSelectedTabIndicator(R.drawable.qa_ic_question_tab_indicator)
        }
    }

    private fun askQuestionClick() {
        btn_ask_question.apply {
            setSingleOnClickListener {
                turnToQuiz()
            }
            pressToZoomOut()
        }
    }

    private fun initScrollText() {
        //搜索滚动词
        viewModel.getScrollerText()
        val loaderView = getTextView("")
        vf_hot_search.addView(loaderView)
        initHotSearch(vf_hot_search, rl_qa_hot_search)
    }

    private fun turnToQuiz() {
        context?.doIfLogin("提问") {
            QuizActivity.activityStart(this@QuestionContainerFragment, "迎新生", REQUEST_LIST_REFRESH_ACTIVITY)
            MobclickAgent.onEvent(context, CyxbsMob.Event.CLICK_ASK)
        }
    }

    private fun initHotSearch(viewFlipper: ViewFlipper, relativeLayout: RelativeLayout) {
        viewModel.hotWords.observe {
            if (it != null && it.isNotEmpty()) {
                viewFlipper.removeAllViews()
                for (i in it) {
                    viewFlipper.addView(getTextView(context?.getString(R.string.qa_search_hot_word_key) + i))
                }
            }
        }

        vf_hot_search.startFlipping()
        relativeLayout.setSingleOnClickListener {
            val hotWord = StringBuilder()
            vf_hot_search.run {
                val allHotWord = (getChildAt(displayedChild) as TextView).text.toString()
                if (allHotWord.length > HOT_WORD_HEAD_LENGTH) {
                    for (i in HOT_WORD_HEAD_LENGTH until allHotWord.length) {
                        hotWord.append(allHotWord[i])
                    }
                }
            }
            SearchActivity.activityStart(this, hotWord.toString(), iv_question_search)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun setCurrentDate(event: CurrentDateInformationEvent) {
        tv_current_time.text = event.time
    }

    override fun onPause() {
        super.onPause()
        vf_hot_search.stopFlipping()
    }

    override val viewModelClass: Class<QuestionContainerViewModel> = QuestionContainerViewModel::class.java
}