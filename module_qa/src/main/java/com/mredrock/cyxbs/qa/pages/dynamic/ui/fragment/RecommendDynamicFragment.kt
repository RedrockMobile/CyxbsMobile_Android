package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.MINE_PERSON_PAGE
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
import kotlinx.android.synthetic.main.qa_fragment_dynamic_recommend.*
import kotlinx.android.synthetic.main.qa_fragment_question_search_result.*

/**
 * @class RecommendDynamicFragment
 * @author YYQF
 * @data 2021/9/24
 * @description 邮问推荐动态页
 **/
class RecommendDynamicFragment : BaseDynamicFragment() {

    private var mTencent: Tencent? = null

    private lateinit var dynamicListRvAdapter: DynamicAdapter
    val footerRvAdapter = FooterRvAdapter { viewModel.retryRecommend() }
    val emptyRvAdapter = EmptyRvAdapter(BaseApp.context.getString(R.string.qa_question_list_empty_hint))

    override fun initView() {
        /*
        * 官方刷新控件不能修改偏移的误差值, 在左右滑动时与 ViewPager2 出现滑动冲突问题
        * 修改 mTouchSlop 可以修改允许的滑动偏移值, 原因可以看 SwipeRefreshLayout 的 1081 行
        * */
        try {
            val field = qa_srl_recommend.javaClass.getDeclaredField("mTouchSlop")
            field.isAccessible = true
            field.set(qa_srl_recommend, 220)
        }catch (e: Exception) { }
    }

    override fun initData() {
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
        initDynamics()
        initObserve()
    }

    override fun getLayoutId() = R.layout.qa_fragment_dynamic_recommend

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
                                    this@RecommendDynamicFragment,
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
                                    this@RecommendDynamicFragment,
                                    dynamic.topic,
                                    dynamic.content,
                                    url,
                                    arrayListOf(pic)
                                )
                            }
                        CommentConfig.COPY_LINK -> {
                            ClipboardController.copyText(
                                this@RecommendDynamicFragment.requireContext(),
                                url
                            )
                        }
                    }
                }
                onTopicListener = { topic, view ->
                    TopicDataSet.getTopicData(topic)?.let {
                        CircleDetailActivity.activityStartFromCircle(
                            this@RecommendDynamicFragment,
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
                            this@RecommendDynamicFragment.context?.let {
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
                            this@RecommendDynamicFragment.activity?.let { it1 ->
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
                onAvatarClickListener = {
                    ARouter.getInstance().build(MINE_PERSON_PAGE)
                        .withString("redid",it)
                        .navigation()
                }
            }

        qa_rv_dynamic_List_recommend.apply {
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
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0||
                            dynamicListRvAdapter.itemCount == 1
                    ) {
                        Log.d("TAG","(RecommendDynamicFragment.kt:174)->1111111111")
                        stopScroll()
                    }
                }
            })
        }

        qa_srl_recommend.setOnRefreshListener {
            viewModel.invalidateRecommendList()
            viewModel.getMyCirCleData()
        }
    }

    private fun initObserve(){
        observeLoading()

        viewModel.ignorePeople.observe(viewLifecycleOwner, {
            if (it == true)
                viewModel.invalidateRecommendList()
        })

        viewModel.deleteTips.observe(viewLifecycleOwner, {
            if (it == true)
                viewModel.invalidateRecommendList()
        })
    }

     private fun observeLoading() = viewModel.apply {

        recommendList.observe(viewLifecycleOwner, {
            dynamicListRvAdapter.submitList(it)
        })

        recommendNetworkState.observe(viewLifecycleOwner, {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        })

        recommendInitialLoad.observe(viewLifecycleOwner, {
            when (it) {
                NetworkState.LOADING -> {
                    qa_srl_recommend.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_srl_recommend.isRefreshing = false
                }
                else -> {
                    qa_srl_recommend.isRefreshing = false
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