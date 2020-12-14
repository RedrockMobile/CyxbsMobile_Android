package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CommentListAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_fragment_dynamic_detail.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic.*

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {

    companion object {
        fun activityStart(fragment: Fragment, dynamicItem: View, data: Dynamic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, dynamicItem, "dynamicItem")
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    intent.putExtra("dynamicItem", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startActivity(intent, opt.toBundle())
                }
            }
        }
    }

    private val getCommentList: () -> Unit = {
        viewModel.getCommentList(dynamic.postId)
    }

    override val isFragmentActivity = false

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()

    private val commentListRvAdapter = CommentListAdapter(listOf(), {})

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }
    private val footerRvAdapter = FooterRvAdapter { getCommentList }

    lateinit var dynamic: Dynamic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_fragment_dynamic_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)
        initObserve()
        initDynamic()
        initReplyList()


        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
    }

    private fun initObserve() {
        viewModel.commentList.observe(this, Observer {
            commentListRvAdapter.refreshData(it)
        })
        viewModel.loadStatus.observe(this, Observer {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        })

        viewModel.loadStatus.observe {
            when (it) {
                NetworkState.LOADING -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = false
                }
                else -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    override fun onBackPressed() {
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        super.onBackPressed()
    }

    private fun initDynamic() {
        dynamic = intent.getParcelableExtra("dynamicItem")
        qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {
            OptionalPopWindow.Builder().with(this)
                    .addOptionAndCallback(CommentConfig.IGNORE) {
                        Toast.makeText(BaseApp.context, "点击了屏蔽", Toast.LENGTH_SHORT).show()
                    }.addOptionAndCallback(CommentConfig.NOTICE) {
                        Toast.makeText(BaseApp.context, "点击了关注", Toast.LENGTH_SHORT).show()
                    }.addOptionAndCallback(CommentConfig.REPORT) {
                        Toast.makeText(BaseApp.context, "点击了举报", Toast.LENGTH_SHORT).show()
                    }.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
        }
        qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
            qa_iv_dynamic_praise_count_image.toggle()
        }
        qa_iv_dynamic_avatar.setAvatarImageFromUrl(dynamic.avatar)
        qa_tv_dynamic_topic.text = dynamic.topic
        qa_tv_dynamic_nickname.text = dynamic.nickName
        qa_tv_dynamic_content.text = dynamic.content
        qa_tv_dynamic_praise_count.text = dynamic.praiseCount.toString()
        qa_tv_dynamic_comment_count.text = dynamic.commentCount.toString()
        qa_tv_dynamic_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), dynamic.publishTime * 1000)
        if (dynamic.pics.isNullOrEmpty())
            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
        else {
            dynamic.pics.apply {
                val tag = qa_dynamic_nine_grid_view.tag
                if (null == tag || tag == this) {
                    val tagStore = qa_dynamic_nine_grid_view.tag
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                } else {
                    val tagStore = this
                    qa_dynamic_nine_grid_view.tag = null
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_NORMAL_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                }
            }

        }
        qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
            ViewImageActivity.activityStart(this, dynamic.pics.map { it }.toTypedArray(), index)
        }
    }


    private fun initReplyList() {

        qa_detail_swipe_refresh_layout.setOnRefreshListener {
            viewModel.commentList.postValue(listOf())
            getCommentList.invoke()
        }

        getCommentList.invoke()

        qa_rv_reply_list.apply {
            layoutManager = LinearLayoutManager(context)

            val adapterWrapper = RvAdapterWrapper(
                    normalAdapter = commentListRvAdapter,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
            adapter = adapterWrapper
        }
    }


}