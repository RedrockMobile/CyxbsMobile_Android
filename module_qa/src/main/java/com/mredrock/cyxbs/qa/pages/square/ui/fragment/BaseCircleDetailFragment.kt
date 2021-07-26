package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
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
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.RELEASE_DYNAMIC_ACTIVITY_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState.Companion.CANNOT_LOAD_WITHOUT_LOGIN
import com.mredrock.cyxbs.qa.network.NetworkState.Companion.LOADING
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_last_hot.*

/**
 *@Date 2020-11-23
 *@Time 21:51
 *@author SpreadWater
 *@description  用于最新和热门的fragment的基类
 */
abstract class BaseCircleDetailFragment<T : CircleDetailViewModel> : BaseViewModelFragment<T>() {

    private var mTencent: Tencent? = null
    lateinit var dynamicListRvAdapter: DynamicAdapter
    private var loop = 1
    private var type = "main"
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.apply {
            loop = getInt("loop")
            type = getString("type", "main")
        }
        super.onCreate(savedInstanceState)
    }

    override fun getViewModelFactory() = CircleDetailViewModel.Factory(type,loop)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qa_fragment_last_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDynamic()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this.requireContext())
    }

    private fun initDynamic() {
        dynamicListRvAdapter = DynamicAdapter(this.requireContext()) { dynamic, view ->
            DynamicDetailActivity.activityStart(this, view, dynamic)
        }.apply {
            onShareClickListener = { dynamic, mode ->
                val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                val pic = if(dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                when (mode) {
                    QQ_FRIEND ->{
                        val pic = if(dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                        mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@BaseCircleDetailFragment, dynamic.topic, dynamic.content, url, pic) }
                    }
                    QQ_ZONE ->
                        mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@BaseCircleDetailFragment, dynamic.topic, dynamic.content, url, arrayListOf(pic)) }
                    COPY_LINK ->{
                        this@BaseCircleDetailFragment.context?.let {
                            ClipboardController.copyText(it, url)
                        }
                    }
                }
            }
            onPopWindowClickListener = { position, string, dynamic ->
                when (string) {
                    IGNORE -> {
                        viewModel.ignore(dynamic)
                    }
                    REPORT -> {
                        this@BaseCircleDetailFragment.context?.let {
                            QaReportDialog(it).apply {
                                show { reportContent ->
                                    viewModel.report(dynamic, reportContent)
                                }
                            }.show()
                        }
                    }
                    UN_FOLLOW->{
                        activityViewModels<CircleDetailViewModel>().value.followStateChangedMarkObservableByActivity.value = false
                        viewModel.invalidateQuestionList()
                    }
                    FOLLOW ->{
                        activityViewModels<CircleDetailViewModel>().value.followStateChangedMarkObservableByActivity.value = true
                        viewModel.invalidateQuestionList()
                    }
                    DELETE -> {
                        this@BaseCircleDetailFragment.activity?.let { it1 ->
                            QaDialog.show(it1, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                                viewModel.deleteId(dynamic.postId, "0")
                            }
                        }
                    }
                }
            }
        }
        activityViewModels<CircleDetailViewModel>().value.followStateChangedMarkObservableByFragment.observe {
            viewModel.invalidateQuestionList()
        }
        viewModel.deleteTips.observeNotNull {
            if (it == true)
                viewModel.invalidateQuestionList()
        }
        viewModel.ignorePeople.observe {
            if (it == true)
                viewModel.invalidateQuestionList()
        }
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_question_list_empty_hint))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = dynamicListRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )
        qa_rv_circle_detail_last_hot.adapter = adapterWrapper
        qa_rv_circle_detail_last_hot.layoutManager = LinearLayoutManager(context)
        //手动添加分割线

        qa_hot_last_swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidateQuestionList()
        }
        viewModel.circleDynamicList.observe {
            dynamicListRvAdapter.submitList(it)
        }
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }
        viewModel.initialLoad.observe {
            when (it) {
                LOADING -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = true
                    (qa_rv_circle_detail_last_hot.adapter as? RvAdapterWrapper)?.apply {
                    }
                    emptyRvAdapter.showHolder(3)
                }
                CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = false
                }
                else -> {
                    qa_hot_last_swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // 从动态详细返回
            DYNAMIC_DETAIL_REQUEST -> {
                if (resultCode == RequestResultCode.NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateQuestionList()
                } else {
                    // 不需要刷新，则更新当前的dynamic为详细页的dynamic（避免出现评论数目不一致的问题）
                    dynamicListRvAdapter
                    dynamicListRvAdapter.curSharedItem?.apply {
                        val dynamic = data?.getParcelableExtra<Dynamic>("refresh_dynamic")
                        dynamic?.let {
                            dynamicListRvAdapter.curSharedDynamic?.commentCount = dynamic.commentCount
                            this.findViewById<TextView>(R.id.qa_tv_dynamic_comment_count).text = it.commentCount.toString()
                        }
                    }
                    dynamicListRvAdapter.notifyDataSetChanged()
                    dynamicListRvAdapter.notifyDataSetChanged()
                }
            }
            // 从发动态返回
            RELEASE_DYNAMIC_ACTIVITY_REQUEST -> {
                if (resultCode == RequestResultCode.NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.invalidateQuestionList()
                }
            }
        }
    }
}