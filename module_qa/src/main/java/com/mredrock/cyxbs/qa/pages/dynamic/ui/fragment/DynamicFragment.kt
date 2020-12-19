package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.content.Intent
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
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mredrock.cyxbs.common.config.CyxbsMob
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig.DELETE
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.NOTICE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CirclesAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.SearchActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.qa_dialog_report.*
import kotlinx.android.synthetic.main.qa_dialog_report.view.*
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
class DynamicFragment : BaseViewModelFragment<DynamicListViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1

        //R.string.qa_search_hot_word_key 长度
        const val HOT_WORD_HEAD_LENGTH = 6
    }

    // 判断rv是否到顶
    protected var isRvAtTop = true
    lateinit var dynamicListRvAdapter: DynamicAdapter
    override fun getViewModelFactory() = DynamicListViewModel.Factory("main")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_dynamic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollText()
        initDynamics()
        initClick()
    }


    private fun initDynamics() {
        var deletePosition = -1
        dynamicListRvAdapter =
                DynamicAdapter(context) { dynamic, view ->
                    DynamicDetailActivity.activityStart(this, view, dynamic)
                    initClick()
                }.apply {
                    onPopWindowClickListener = { position, string, dynamic ->
                        when (string) {
                            IGNORE -> {
                                viewModel.ignore(dynamic)
                            }
                            REPORT -> {
                                this@DynamicFragment.activity?.let {
                                    QaReportDialog.show(it) { reportContent ->
                                        viewModel.report(dynamic, reportContent)
                                    }
                                }
                            }
                            NOTICE -> {
                                viewModel.followCircle(dynamic)
                            }
                            DELETE -> {
                                this@DynamicFragment.activity?.let { it1 ->
                                    QaDialog.show(it1, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                                        viewModel.deleteId(dynamic.postId, "0")
                                        deletePosition = position
                                    }
                                }
                            }
                        }
                    }
                }

        viewModel.deleteTips.observe {
            if (deletePosition != -1)
                dynamicListRvAdapter.notifyItemRemoved(deletePosition)
        }
        viewModel.followCircle.observe {
            viewModel.getMyCirCleData()
        }

        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = dynamicListRvAdapter.let {
            RvAdapterWrapper(
                    normalAdapter = it,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
        }
        val circlesAdapter = this.activity?.let { CirclesAdapter() }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_circles_List.apply {
            layoutManager = linearLayoutManager
            adapter = circlesAdapter
        }
        viewModel.getMyCirCleData()
        viewModel.myCircle.observe {
            if (!it.isNullOrEmpty()) {
                val layoutParams = CollapsingToolbarLayout.LayoutParams(rv_circles_List.layoutParams)
                layoutParams.topMargin = 70
                layoutParams.bottomMargin = 30
                rv_circles_List.layoutParams = layoutParams
                tv_my_notice.visibility = View.VISIBLE
                view_divide.visibility = View.VISIBLE
                circlesAdapter?.addData(it)
            } else {
                val layoutParams = CollapsingToolbarLayout.LayoutParams(rv_circles_List.layoutParams)
                layoutParams.bottomMargin = 30
                view_divide.visibility = View.VISIBLE
                rv_circles_List.layoutParams = layoutParams
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


        swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidateQuestionList()
            viewModel.getMyCirCleData()
        }
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
            QuizActivity.activityStart(this, "发动态", REQUEST_LIST_REFRESH_ACTIVITY)
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop)
            viewModel.invalidateQuestionList()
        else
            rv_dynamic_List.smoothScrollToPosition(0)

    }

    override fun onPause() {
        super.onPause()
        vf_hot_search.stopFlipping()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            DYNAMIC_DETAIL_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateQuestionList()
                    viewModel.getMyCirCleData()
                } else {
                    // 不需要刷新，则更新当前的dynamic为详细页的dynamic（避免出现评论数目不一致的问题）
                    dynamicListRvAdapter.notifyDataSetChanged()
                }
            }
            // 从发动态返回
            RELEASE_DYNAMIC_ACTIVITY_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateQuestionList()
                    viewModel.getMyCirCleData()

                }
            }
        }
    }

}