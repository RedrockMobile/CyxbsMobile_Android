package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.CyxbsMob
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.config.StoreTask
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.COPY_LINK
import com.mredrock.cyxbs.qa.config.CommentConfig.DELETE
import com.mredrock.cyxbs.qa.config.CommentConfig.FOLLOW
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_FRIEND
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_ZONE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.config.CommentConfig.UN_FOLLOW
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.config.RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CirclesAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.search.ui.SearchActivity
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleSquareActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.qa_dialog_report.*
import kotlinx.android.synthetic.main.qa_dialog_report.view.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Author: xgl
 * @ClassName: DynamicFragment
 * @Description:
 * @Date: 2020/11/16 22:07
 */
@Route(path = QA_ENTRY)
open class DynamicFragment : BaseViewModelFragment<DynamicListViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val REQUEST_LIST_REFRESH_ACTIVITY = 0x1

        //R.string.qa_search_hot_word_key 长度
        const val HOT_WORD_HEAD_LENGTH = 6
    }

    //用来将发送调节window alpha的handler,如果任务还未执行就返回到了这个window就马上取消任务
    private lateinit var handler: Handler
    private lateinit var windowAlphaRunnable: Runnable

    private var isSendDynamic = false
    private var mTencent: Tencent? = null
    private var token: String? = null

    // 判断rv是否到顶
    var isRvAtTop = true
    private lateinit var dynamicListRvAdapter: DynamicAdapter
    override fun getViewModelFactory() = DynamicListViewModel.Factory("recommend")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handler = Handler()
        windowAlphaRunnable = Runnable()
        {
            val window: Window = requireActivity().window
            val layoutParams: WindowManager.LayoutParams = window.attributes
            layoutParams.alpha = 0F
            window.attributes = layoutParams
        }
        return inflater.inflate(R.layout.qa_fragment_dynamic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollText()
        initDynamics()
        initClick()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
        val accountService = ServiceManager.getService(IAccountService::class.java)
        token = accountService.getUserTokenService().getToken()
    }


    private fun initDynamics() {
        viewModel.getAllCirCleData("问答圈", "test1")
        dynamicListRvAdapter =
            DynamicAdapter(this.requireContext()) { dynamic, view ->
                StoreTask.postTask(StoreTask.Task.SEE_DYNAMIC, dynamic.postId) // 浏览动态修改任务进度
                DynamicDetailActivity.activityStart(this, view, dynamic)
            }.apply {
                onShareClickListener = { dynamic, mode ->
                    val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                    when (mode) {
                        QQ_FRIEND -> {
                            val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                            mTencent?.let { it1 ->
                                ShareUtils.qqShare(
                                    it1,
                                    this@DynamicFragment,
                                    dynamic.topic,
                                    dynamic.content,
                                    url,
                                    pic
                                )
                            }
                        }
                        QQ_ZONE ->
                            mTencent?.let { it1 ->
                                ShareUtils.qqQzoneShare(
                                    it1,
                                    this@DynamicFragment,
                                    dynamic.topic,
                                    dynamic.content,
                                    url,
                                    ArrayList(dynamic.pics)
                                )
                            }
                        COPY_LINK -> {
                            ClipboardController.copyText(this@DynamicFragment.requireContext(), url)
                        }
                    }
                }
                onTopicListener = { topic, view ->
                    TopicDataSet.getTopicData(topic)?.let {
                        CircleDetailActivity.activityStartFromCircle(
                            this@DynamicFragment,
                            it
                        )
                    }
                }
                onPopWindowClickListener = { _, string, dynamic ->
                    when (string) {
                        IGNORE -> {
                            viewModel.ignore(dynamic)
                        }
                        REPORT -> {
                            this@DynamicFragment.context?.let {
                                QaReportDialog(it).apply {
                                    show { reportContent ->
                                        viewModel.report(dynamic, reportContent)
                                    }
                                }.show()
                            }
                        }
                        FOLLOW -> {
                            viewModel.followGroup(dynamic.topic, false)
                        }
                        UN_FOLLOW -> {
                            viewModel.followGroup(dynamic.topic, true)
                        }
                        DELETE -> {
                            this@DynamicFragment.activity?.let { it1 ->
                                QaDialog.show(
                                    it1,
                                    resources.getString(R.string.qa_dialog_tip_delete_comment_text),
                                    {}) {
                                    viewModel.deleteId(dynamic.postId, "0")
                                }
                            }
                        }
                    }
                }
            }

        viewModel.ignorePeople.observe {
            if (it == true)
                viewModel.invalidateDynamicList()
        }

        viewModel.deleteTips.observe {
            if (it == true)
                viewModel.invalidateDynamicList()
        }
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
            normalAdapter = dynamicListRvAdapter,
            emptyAdapter = emptyRvAdapter,
            footerAdapter = footerRvAdapter
        )
        val circlesAdapter = this.activity?.let {
            CirclesAdapter({ topic, view ->
                if (topic.topicId == "0") {
                    CircleSquareActivity.activityStartFromDynamic(this)
                } else
                    CircleDetailActivity.activityStartFromCircle(this, topic)
            }, this)
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        qa_rv_circles_List.apply {
            layoutManager = linearLayoutManager
            adapter = circlesAdapter
        }

        //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
        refreshTopicMessage()
        viewModel.topicMessageList.observe {
            if (it != null) {
                circlesAdapter?.addTopicMessageData(it)
            }
        }

        viewModel.getMyCirCleData()

        viewModel.myCircle.observe {
            if (!it.isNullOrEmpty()) {
                val layoutParams =
                    CollapsingToolbarLayout.LayoutParams(qa_rv_circles_List.layoutParams)
                layoutParams.topMargin = 70
                layoutParams.bottomMargin = 30
                layoutParams.height = BaseApp.context.dp2px(130f)
                qa_rv_circles_List.setPadding(0, 0, 0, BaseApp.context.dp2px(30f))
                qa_rv_circles_List.layoutParams = layoutParams
                qa_tv_my_notice.visibility = View.VISIBLE
                view_divide.visibility = View.VISIBLE
                circlesAdapter?.addCircleData(it)
            } else {
                val layoutParams =
                    CollapsingToolbarLayout.LayoutParams(qa_rv_circles_List.layoutParams)
                layoutParams.height = BaseApp.context.dp2px(110f)
                qa_rv_circles_List.setPadding(0, 0, 0, BaseApp.context.dp2px(0f))
                circlesAdapter?.noticeChangeCircleData()
                view_divide.visibility = View.GONE
                qa_tv_my_notice.visibility = View.INVISIBLE
                qa_rv_circles_List.layoutParams = layoutParams
                qa_rv_circles_List.invalidate()
            }
        }

        observeLoading(dynamicListRvAdapter, footerRvAdapter, emptyRvAdapter)
        qa_rv_dynamic_List.apply {
            val animator = itemAnimator // 取消 RecyclerView 刷新的动画, 因为会出现图片闪动问题
            if (animator != null) {
                animator.changeDuration = 0
                animator.removeDuration = 0
                animator.moveDuration = 0
                animator.addDuration = 0
            }
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
            refreshTopicMessage()
            viewModel.invalidateDynamicList()
            viewModel.getMyCirCleData()
        }
    }

    private fun refreshTopicMessage() {
        //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
        if (!TopicDataSet.getOutCirCleDetailTime().isNullOrEmpty()) {
            TopicDataSet.getOutCirCleDetailTime()?.let { viewModel.getTopicMessages(it) }
        }
    }

    private fun observeLoading(
        dynamicListRvAdapter: DynamicAdapter,
        footerRvAdapter: FooterRvAdapter,
        emptyRvAdapter: EmptyRvAdapter
    ): DynamicListViewModel = viewModel.apply {
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
                NetworkState.SUCCESSFUL ->
                    isSendDynamic = true
                NetworkState.FAILED ->
                    isSendDynamic = false
            }
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
        qa_bt_to_quiz.setOnSingleClickListener {
            if (isSendDynamic)
                turnToQuiz()
            else {
                CyxbsToast.makeText(this.activity, R.string.qa_server_go_out, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //如果进入搜索界面马上返回需要立刻取消任务
        handler.removeCallbacks(windowAlphaRunnable)

        //调节当前window alpha值
        val window: Window = requireActivity().window
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.alpha = 1F
        window.attributes = layoutParams

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

    override fun onPause() {
        super.onPause()
        vf_hot_search.stopFlipping()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            DYNAMIC_DETAIL_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新则刷新显示动态
                    viewModel.getAllCirCleData("问答圈", "test1")
                    //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
                    viewModel.getMyCirCleData()
                    refreshTopicMessage()
                    viewModel.invalidateDynamicList()
                } else {
                    // 不需要刷新，则更新当前的dynamic为详细页的dynamic（避免出现评论数目不一致的问题）
                    dynamicListRvAdapter.curSharedItem?.apply {
                        val dynamic = data?.getParcelableExtra<Dynamic>("refresh_dynamic")
                        dynamic?.let {
                            // 进行判断，如果返回的数据评论数和当前的不一样才回去刷新列表
                            if (dynamicListRvAdapter.curSharedDynamic?.commentCount != it.commentCount) {
                                dynamicListRvAdapter.curSharedDynamic?.commentCount =
                                    it.commentCount
                                this.findViewById<TextView>(R.id.qa_tv_dynamic_comment_count).text =
                                    it.commentCount.toString()
                                dynamicListRvAdapter.notifyItemChanged(dynamicListRvAdapter.curSharedItemPosition, "")
                            }
                        }
                    }
//                    dynamicListRvAdapter.notifyDataSetChanged()
                }
            }
            // 从发动态返回
            RELEASE_DYNAMIC_ACTIVITY_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.getAllCirCleData("问答圈", "test1")
                    //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
                    viewModel.getMyCirCleData()
                    refreshTopicMessage()
                    viewModel.invalidateDynamicList()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun refreshQuestionList(event: RefreshQaEvent) {
        if (isRvAtTop)
            viewModel.invalidateDynamicList()
        else
            qa_rv_dynamic_List.smoothScrollToPosition(0)
    }

}