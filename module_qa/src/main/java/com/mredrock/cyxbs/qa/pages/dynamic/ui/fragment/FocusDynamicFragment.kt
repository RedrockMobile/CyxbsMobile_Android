package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.content.Intent
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleDetailActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_dynamic_focus.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic_recommend.*
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*

/**
 * @class FocusDynamicFragment
 * @author YYQF
 * @data 2021/9/24
 * @description 邮问关注动态页
 **/
class FocusDynamicFragment : BaseDynamicFragment() {

    private var mTencent: Tencent? = null

    private lateinit var dynamicListRvAdapter: DynamicAdapter
    val footerRvAdapter = FooterRvAdapter { viewModel.retryRecommend() }
    val emptyRvAdapter = EmptyRvAdapter(BaseApp.context.getString(R.string.qa_question_list_empty_hint))

    override fun initData() {
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
        initDynamics()
        initObserve()
    }

    override fun getLayoutId() = R.layout.qa_fragment_dynamic_focus

    private fun initDynamics() {
        dynamicListRvAdapter =
            DynamicAdapter(this.requireContext()) { dynamic, view ->
                DynamicDetailActivity.activityStart(this, view, dynamic)
            }.apply {

                onShareClickListener = { dynamic, mode ->
                    val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                    val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                    when (mode) {
                        CommentConfig.QQ_FRIEND -> {
                            mTencent?.let { it1 ->
                                ShareUtils.qqShare(
                                    it1,
                                    this@FocusDynamicFragment,
                                    dynamic.topic,
                                    dynamic.content,
                                    url,
                                    pic
                                )
                            }
                        }
                        CommentConfig.QQ_ZONE ->
                            mTencent?.let { it1 ->
                                ShareUtils.qqQzoneShare(
                                    it1,
                                    this@FocusDynamicFragment,
                                    dynamic.topic,
                                    dynamic.content,
                                    url,
                                    arrayListOf(pic)
                                )
                            }
                        CommentConfig.COPY_LINK -> {
                            ClipboardController.copyText(
                                this@FocusDynamicFragment.requireContext(),
                                url
                            )
                        }
                    }
                }
                onTopicListener = { topic, view ->
                    TopicDataSet.getTopicData(topic)?.let {
                        CircleDetailActivity.activityStartFromCircle(
                            this@FocusDynamicFragment,
                            view,
                            it
                        )
                    }
                }
                onPopWindowClickListener = { _, string, dynamic ->
                    when (string) {
                        CommentConfig.IGNORE -> {
                            viewModel.ignore(dynamic)
                        }
                        CommentConfig.REPORT -> {
                            this@FocusDynamicFragment.context?.let {
                                QaReportDialog(it).apply {
                                    show { reportContent ->
                                        viewModel.report(dynamic, reportContent)
                                    }
                                }.show()
                            }
                        }
                        CommentConfig.FOLLOW -> {
                            viewModel.followGroup(dynamic.topic, false)
                        }
                        CommentConfig.UN_FOLLOW -> {
                            viewModel.followGroup(dynamic.topic, true)
                        }
                        CommentConfig.DELETE -> {
                            this@FocusDynamicFragment.activity?.let { it1 ->
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

        qa_rv_dynamic_List_focus.apply {
            val animator = itemAnimator // 取消 RecyclerView 刷新的动画, 因为会出现图片闪动问题
            if (animator != null) {
                animator.changeDuration = 0
                animator.removeDuration = 0
                animator.moveDuration = 0
                animator.addDuration = 0
            }
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            adapter = RvAdapterWrapper(
                normalAdapter = dynamicListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
            )
            /**
             * 解决嵌套滑动中RecyclerView滑到顶部时不能立刻左右滑动, 因为RecyclerView的惯性滑动还在继续
             */
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        stopScroll()
                    }
                }
            })
        }

        qa_srl_focus.setOnRefreshListener {
            viewModel.invalidateFocusList()
            viewModel.getMyCirCleData()
        }
    }

    private fun initObserve(){
        observeLoading()

        viewModel.ignorePeople.observe(viewLifecycleOwner, {
            if (it == true)
                viewModel.invalidateFocusList()
        })

        viewModel.deleteTips.observe(viewLifecycleOwner, {
            if (it == true)
                viewModel.invalidateFocusList()
        })
    }

    private fun observeLoading() = viewModel.apply {
        focusList.observe(viewLifecycleOwner, {
            dynamicListRvAdapter.submitList(it)
        })

        focusNetworkState.observe(viewLifecycleOwner, {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        })

        focusInitialLoad.observe(viewLifecycleOwner, {
            when (it) {
                NetworkState.LOADING -> {
                    qa_srl_focus.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_srl_focus.isRefreshing = false
                }
                else -> {
                    qa_srl_focus.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        })
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            DYNAMIC_DETAIL_REQUEST -> {
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
                            dynamicListRvAdapter.notifyItemChanged(
                                dynamicListRvAdapter.curSharedItemPosition,
                                ""
                            )
                        }
                    }
                }
            }
        }
    }
}