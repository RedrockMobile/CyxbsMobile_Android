package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.CyxbsMob
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CirclesAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.SearchActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
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
class DynamicFragment : BaseViewModelFragment<DynamicListViewModel>() {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1

        //R.string.qa_search_hot_word_key 长度
        const val HOT_WORD_HEAD_LENGTH = 6
    }

    // 判断rv是否到顶
    protected var isRvAtTop = true

    //判断是否加载过热词，首次加载fragment，会加载一次，设置true，onPause就不会加载
    // onStop设置为false，onPause就会加载

    override fun getViewModelFactory() = DynamicListViewModel.Factory("main")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_dynamic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollText()
        initDynamic()
        initClick()
    }

    private fun initDynamic() {
        val dynamicListRvAdapter = DynamicAdapter { dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = dynamicListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        val circlesAdapter = this.activity?.let { CirclesAdapter(it) }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_circles_List.apply {
            layoutManager = linearLayoutManager
            adapter = circlesAdapter
        }
        viewModel.getCirCleData()
        viewModel.circlesItem.observe {
            if (it != null) {
                if (it.isNotEmpty()) {
                    tv_my_notice.visibility = View.VISIBLE
                    circlesAdapter?.addData(it as ArrayList)
                }
            }
        }
        observeLoading(dynamicListRvAdapter, footerRvAdapter, emptyRvAdapter)
        rv_dynamic_List.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterWrapper
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isRvAtTop = !recyclerView.canScrollVertically(-1)
                }
            })
        }
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateQuestionList() }
    }

    open fun observeLoading(dynamicListRvAdapter: DynamicAdapter,
                            footerRvAdapter: FooterRvAdapter,
                            emptyRvAdapter: EmptyRvAdapter): DynamicListViewModel = viewModel.apply {
        dynamicList.observe {
            dynamicListRvAdapter.submitList(it)
        }
        networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    (rv_dynamic_List.adapter as? RvAdapterWrapper)?.apply {

                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    swipe_refresh_layout.isRefreshing = false
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop)
            viewModel.invalidateQuestionList()
        else
            rv_dynamic_List.smoothScrollToPosition(0)

    }

    private fun initClick() {
        bt_to_quiz.setOnSingleClickListener {
            turnToQuiz()
        }
    }

    override fun onResume() {
        super.onResume()
        vf_hot_search.startFlipping()
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
            QuizActivity.activityStart(this, "迎新生", REQUEST_LIST_REFRESH_ACTIVITY)
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


    override fun onPause() {
        super.onPause()
        vf_hot_search.stopFlipping()
    }

}