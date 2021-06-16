package com.mredrock.cyxbs.qa.pages.mine.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.config.QA_DYNAMIC_DETAIL
import com.mredrock.cyxbs.common.config.QA_DYNAMIC_MINE
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.SHARE_URL
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyDynamicViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_activity_my_dynamic.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*

@Route(path = QA_DYNAMIC_MINE)
class MyDynamicActivity : BaseViewModelActivity<MyDynamicViewModel>() {

    private var isRvAtTop = true
    private var isSendDynamic = false
    lateinit var dynamicListRvAdapter: DynamicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_dynamic)
        initView()
        initDynamics()
    }

    private fun initView() {
        qa_tv_toolbar_title.text = "动态"
        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
        qa_my_dynamic_toolbar.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun initDynamics() {
        val mTencent = Tencent.createInstance(CommentConfig.APP_ID, this)
        dynamicListRvAdapter =
                DynamicAdapter(this) { dynamic, view ->
                    DynamicDetailActivity.activityStart(this, view, dynamic)
                }.apply {

                    onShareClickListener = { dynamic, mode ->
                        val url = "${SHARE_URL}dynamic?id=${dynamic.postId}"
                        when (mode) {
                            CommentConfig.QQ_FRIEND ->{
                                val pic = if(dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                                mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@MyDynamicActivity, dynamic.topic, dynamic.content, url, pic) }
                            }
                            CommentConfig.QQ_ZONE ->
                                mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@MyDynamicActivity, dynamic.topic, dynamic.content, url, ArrayList(dynamic.pics)) }
                            CommentConfig.COPY_LINK -> {
                                ClipboardController.copyText(this@MyDynamicActivity, url)
                            }
                        }
                    }
                    onPopWindowClickListener = { position, string, dynamic ->
                        when (string) {
                            CommentConfig.IGNORE -> {
                                viewModel.ignore(dynamic)
                            }
                            CommentConfig.REPORT -> {
                                QaReportDialog(this@MyDynamicActivity).apply {
                                    show { reportContent ->
                                        viewModel.report(dynamic, reportContent)
                                    }
                                }.show()
                            }
                            CommentConfig.DELETE -> {
                                this@MyDynamicActivity.let { it1 ->
                                    QaDialog.show(it1, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                                        viewModel.deleteId(dynamic.postId, "0")
                                    }
                                }
                            }
                        }
                    }
                }

        qa_rv_my_dynamic.adapter = dynamicListRvAdapter
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
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        //获取用户进入圈子详情退出的时间，去请求从而刷新未读消息
        if (!TopicDataSet.getOutCirCleDetailTime().isNullOrEmpty()) {
            LogUtils.d("outTime", TopicDataSet.getOutCirCleDetailTime().toString())
            TopicDataSet.getOutCirCleDetailTime()?.let { viewModel.getTopicMessages(it) }
        }

        observeLoading(dynamicListRvAdapter, footerRvAdapter, emptyRvAdapter)
        qa_rv_my_dynamic.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterWrapper
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isRvAtTop = !recyclerView.canScrollVertically(-1)
                }
            })
        }

        qa_swl_my_dynamic.setOnRefreshListener {
            if (!TopicDataSet.getOutCirCleDetailTime().isNullOrEmpty()) {
                LogUtils.d("swipeOutTime", TopicDataSet.getOutCirCleDetailTime().toString())
                TopicDataSet.getOutCirCleDetailTime()?.let { viewModel.getTopicMessages(it) }
            }
            viewModel.invalidateDynamicList()
            viewModel.getMyCirCleData()
        }
    }

    fun observeLoading(dynamicListRvAdapter: DynamicAdapter,
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
                NetworkState.SUCCESSFUL ->
                    isSendDynamic = true
                NetworkState.FAILED ->
                    isSendDynamic = false
            }
            when (it) {
                NetworkState.LOADING -> {
                    qa_swl_my_dynamic.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_swl_my_dynamic.isRefreshing = false
                }
                else -> {
                    qa_swl_my_dynamic.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DYNAMIC_DETAIL_REQUEST){
            dynamicListRvAdapter.curSharedItem?.apply {
                val dynamic = data?.getParcelableExtra<Dynamic>("refresh_dynamic")
                dynamic?.let {
                    dynamicListRvAdapter.curSharedDynamic?.commentCount = dynamic.commentCount
                    this.findViewById<TextView>(R.id.qa_tv_dynamic_comment_count).text = it.commentCount.toString()
                }
            }
            dynamicListRvAdapter.notifyDataSetChanged()
        }
    }
}