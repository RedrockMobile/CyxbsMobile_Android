
package com.mredrock.cyxbs.qa.pages.mine.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_DYNAMIC_MINE_FRAGMENT
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyDynamicViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_activity_my_dynamic.*

@Route(path = QA_DYNAMIC_MINE_FRAGMENT)
class MyDynamicFragment : BaseViewModelFragment<MyDynamicViewModel>() {
    private var isRvAtTop = true
    private var isSendDynamic = false
    private lateinit var dynamicListRvAdapter: DynamicAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.qa_fragment_mine_dynamic, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDynamics()
    }

    private fun initDynamics() {
        val mTencent = Tencent.createInstance(CommentConfig.APP_ID, context)
        dynamicListRvAdapter =
            DynamicAdapter(context) { dynamic, view ->
                DynamicDetailActivity.activityStart(this, view, dynamic)
            }.apply {

                onShareClickListener = { dynamic, mode ->
                    val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                    when (mode) {
                        CommentConfig.QQ_FRIEND -> {
                            val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                            mTencent?.let { it1 ->
                                activity?.let {
                                    ShareUtils.qqShare(
                                        it1,
                                        it,
                                        dynamic.topic,
                                        dynamic.content,
                                        url,
                                        pic
                                    )
                                }
                            }
                        }
                        CommentConfig.QQ_ZONE ->
                            mTencent?.let { it1 ->
                                activity?.let {
                                    ShareUtils.qqQzoneShare(
                                        it1,
                                        it,
                                        dynamic.topic,
                                        dynamic.content,
                                        url,
                                        ArrayList(dynamic.pics)
                                    )
                                }
                            }
                        CommentConfig.COPY_LINK -> {
                            activity?.let { ClipboardController.copyText(it, url) }
                        }
                    }
                }
                onPopWindowClickListener = { _, string, dynamic ->
                    when (string) {
                        CommentConfig.DELETE -> {
                            activity.let { it1 ->
                                it1?.let {
                                    QaDialog.show(
                                        it,
                                        resources.getString(R.string.qa_dialog_tip_delete_comment_text),
                                        {}) {
                                        viewModel.deleteId(dynamic.postId, "0")
                                    }
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
    }

    private fun observeLoading(
        dynamicListRvAdapter: DynamicAdapter,
        footerRvAdapter: FooterRvAdapter,
        emptyRvAdapter: EmptyRvAdapter
    ): MyDynamicViewModel = viewModel.apply {
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
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                }
                else -> {
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }
}
































