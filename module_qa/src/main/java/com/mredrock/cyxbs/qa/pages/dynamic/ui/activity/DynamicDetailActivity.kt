package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.ReplyInfo
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.DYNAMIC_DETAIL_REQUEST
import com.mredrock.cyxbs.qa.config.RequestResultCode.NEED_REFRESH_RESULT
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CommentListAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.*
import com.mredrock.cyxbs.qa.utils.ClipboardController
import com.mredrock.cyxbs.qa.utils.KeyboardController
import com.mredrock.cyxbs.qa.utils.ShareUtils
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_activity_dynamic_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*


/**
 * @Author: zhangzhe
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {

    companion object {
        var dynamicOrigin: Dynamic? = null
        fun activityStart(fragment: Fragment, dynamicItem: View, data: Dynamic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, dynamicItem, "dynamicItem")
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    dynamicOrigin = data
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST, opt.toBundle())
                }
            }
        }

        const val DYNAMIC_DELETE = "0"
        const val COMMENT_DELETE = "1"
    }

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }

    private val footerRvAdapter = FooterRvAdapter { refreshCommentList() }

    lateinit var dynamic: Dynamic

    private val behavior by lazy { AppBarLayout.ScrollingViewBehavior() }

    override val isFragmentActivity = false

    private var mTencent: Tencent? = null

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()


    private fun refreshCommentList() {
        viewModel.refreshCommentList(dynamic.postId, "-1")
    }

    // 评论点击的逻辑
    private val commentListRvAdapter = CommentListAdapter(
            onItemClickEvent = { comment ->
                viewModel.replyInfo.value = ReplyInfo(comment.nickName, comment.content, comment.commentId)
            },
            onReplyInnerClickEvent = { comment ->
                viewModel.replyInfo.value = ReplyInfo(comment.nickName, comment.content, comment.commentId)
            },
            onItemLongClickEvent = { comment, itemView ->
                // 消除回复弹窗
                viewModel.replyInfo.value = ReplyInfo("", "", "")

                val optionPopWindow = OptionalPopWindow.Builder().with(this)
                        .addOptionAndCallback(CommentConfig.REPLY) {
                            viewModel.replyInfo.value = ReplyInfo(comment.nickName, comment.content, comment.commentId)
                        }.addOptionAndCallback(CommentConfig.COPY) {
                            ClipboardController.copyText(this, comment.content)
                        }
                if (dynamic.isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            viewModel.deleteId(comment.commentId, COMMENT_DELETE)
                        }
                    }
                } else {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog.show(this) {
                            viewModel.report(comment.commentId, it, CommentConfig.REPORT_COMMENT_MODEL)
                        }
                    }
                }
                optionPopWindow.show(itemView, OptionalPopWindow.AlignMode.CENTER, 0)

            },
            onReplyInnerLongClickEvent = { comment, itemView ->
                // 消除回复弹窗
                viewModel.replyInfo.value = ReplyInfo("", "", "")

                val optionPopWindow = OptionalPopWindow.Builder().with(this)
                        .addOptionAndCallback(CommentConfig.REPLY) {
                            viewModel.replyInfo.value = ReplyInfo(comment.nickName, comment.content, comment.commentId)
                        }.addOptionAndCallback(CommentConfig.COPY) {
                            ClipboardController.copyText(this, comment.content)
                        }
                if (dynamic.isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            viewModel.deleteId(comment.commentId, COMMENT_DELETE)
                        }
                    }
                } else {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog.show(this) {
                            viewModel.report(comment.commentId, it, CommentConfig.REPORT_COMMENT_MODEL)
                        }
                    }
                }
                optionPopWindow.show(itemView, OptionalPopWindow.AlignMode.CENTER, 0)

            },
            onMoreReplyClickEvent = { commentId -> ReplyDetailActivity.activityStart(this, viewModel, commentId, null) }
    )


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_dynamic_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)
        initObserve()
        initDynamic()
        initReplyList()
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this)
        qa_iv_select_pic.setOnSingleClickListener {
            Intent(this, QuizActivity::class.java).apply {
                putExtra("isComment", "1")
                putExtra("postId", dynamic.postId)
                putExtra("replyId", viewModel.replyInfo.value?.replyId ?: "")
                putExtra("commentContent", qa_et_reply.text.toString())
                putExtra("replyNickname", viewModel.replyInfo.value?.nickname ?: "")
                startActivityForResult(this, RequestResultCode.RELEASE_COMMENT_ACTIVITY_REQUEST)
            }
        }

        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }

        qa_btn_send.setOnSingleClickListener {
            viewModel.releaseComment(dynamic.postId, qa_et_reply.text.toString())
        }
        qa_iv_dynamic_comment_count.setOnSingleClickListener {
            KeyboardController.showInputKeyboard(this, qa_et_reply)
            viewModel.replyInfo.value = ReplyInfo("", "", "")
        }
        qa_coordinatorlayout.onReplyCancelEvent = {
            viewModel.replyInfo.value = ReplyInfo("", "", "")
            KeyboardController.hideInputKeyboard(this, qa_et_reply)
        }
    }

    private fun initObserve() {
        viewModel.commentList.observe(this, Observer {
            if (it != null) {
                commentListRvAdapter.refreshData(it)
                if (viewModel.position != -1) {
                    qa_rv_comment_list.smoothScrollToPosition(viewModel.position)
                    qa_appbar_dynamic.setExpanded(false, true)
                } else {
                    qa_appbar_dynamic.setExpanded(true, false)
                }
            }
        })


        viewModel.loadStatus.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

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
        viewModel.replyInfo.observe {
            it?.let {
                if (it.replyId.isNotEmpty()) {
                    qa_coordinatorlayout.isReplyEdit = true
                    KeyboardController.showInputKeyboard(this@DynamicDetailActivity, qa_et_reply)
                    ReplyPopWindow.with(this@DynamicDetailActivity)
                    ReplyPopWindow.setReplyName(it.nickname, it.content)
                    ReplyPopWindow.setOnClickEvent {
                        viewModel.replyInfo.value = ReplyInfo("", "", "")
                    }
                    ReplyPopWindow.show(qa_et_reply, ReplyPopWindow.AlignMode.LEFT, this@DynamicDetailActivity.dp2px(6f))
                } else {
                    if (ReplyPopWindow.isShowing()) {
                        ReplyPopWindow.dismiss()
                    }
                }

            }
        }
        viewModel.commentReleaseResult.observe {
            viewModel.replyInfo.value = ReplyInfo("", "", "")
            qa_et_reply.setText("")
            KeyboardController.hideInputKeyboard(this, qa_et_reply)

            viewModel.refreshCommentList(dynamic.postId, (it?.commentId
                    ?: -1).toString())

        }
        viewModel.deleteDynamic.observe {
            // 通知主页面刷新
            setResult(NEED_REFRESH_RESULT)
            finish()
        }
    }

    override fun onBackPressed() {
        if (ReplyPopWindow.isShowing()) {
            ReplyPopWindow.dismiss()
            return
        }
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        finish()
        super.onBackPressed()
    }

    private fun initDynamic() {

        // 设置behavior
        val layoutParams = qa_ll_reply.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = behavior


        dynamic = dynamicOrigin!!
        viewModel.dynamicLiveData.value = dynamicOrigin!!
        viewModel.dynamicLiveData.observe {
            dynamic = it!!
            dynamicOrigin?.commentCount = it.commentCount
            refreshDynamic()
        }
        refreshDynamic()

        qa_tv_reply_title.setOnClickListener {
            KeyboardController.showInputKeyboard(this, qa_et_reply)
        }

        qa_iv_dynamic_share.setOnSingleClickListener {
            ShareDialog(it.context).apply {
                initView(onCancelListener = View.OnClickListener {
                    dismiss()
                }, qqshare = View.OnClickListener {
                    mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@DynamicDetailActivity, dynamic.topic, dynamic.content, "https://cn.bing.com/", "") }
                }, qqZoneShare = View.OnClickListener {
                    mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@DynamicDetailActivity, dynamic.topic, dynamic.content, "https://cn.bing.com/", ArrayList()) }

                }, weChatShare = View.OnClickListener {
                    CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()

                }, friendShipCircle = View.OnClickListener {
                    CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()

                }, copylink = View.OnClickListener {
                    ClipboardController.copyText(this@DynamicDetailActivity, "https://cn.bing.com/")

                })
            }.show()
        }

        qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {

            // 消除回复弹窗
            viewModel.replyInfo.value = ReplyInfo("", "", "")

            val optionPopWindow = OptionalPopWindow.Builder().with(this)
                    .addOptionAndCallback(CommentConfig.REPLY) {
                        KeyboardController.showInputKeyboard(this, qa_et_reply)
                        viewModel.replyInfo.value = ReplyInfo("", "", "")
                    }.addOptionAndCallback(CommentConfig.COPY) {
                        ClipboardController.copyText(this, dynamic.content)
                    }
            if (dynamic.isSelf == 1) {
                optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                    QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_dynamic_text), {}) {
                        viewModel.deleteId(dynamic.postId, DYNAMIC_DELETE)
                    }
                }
            } else {
                optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                    QaReportDialog.show(this) { reportText ->
                        viewModel.report(dynamic.postId, reportText, CommentConfig.REPORT_DYNAMIC_MODEL)
                    }
                }
            }
            optionPopWindow.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
        }
        qa_iv_dynamic_praise_count_image.registerLikeView(dynamic.postId, CommentConfig.PRAISE_MODEL_DYNAMIC, dynamic.isPraised, dynamic.praiseCount)
        qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
            qa_iv_dynamic_praise_count_image.click()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun refreshDynamic() {
        qa_iv_dynamic_avatar.setAvatarImageFromUrl(dynamic.avatar)
        qa_tv_dynamic_topic.text = "# " + dynamic.topic
        qa_tv_dynamic_nickname.text = dynamic.nickName
        qa_tv_dynamic_content.text = dynamic.content
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
            viewModel.commentList.value = listOf()
            refreshCommentList()
        }

        refreshCommentList()

        qa_rv_comment_list.apply {
            layoutManager = LinearLayoutManager(context)

            val adapterWrapper = RvAdapterWrapper(
                    normalAdapter = commentListRvAdapter,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
            adapter = adapterWrapper
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // 从动态详细返回
            RequestResultCode.RELEASE_COMMENT_ACTIVITY_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.refreshCommentList(dynamic.postId, "-1")
                    viewModel.replyInfo.value = ReplyInfo("", "", "")
                }
                data?.getStringExtra("text")?.let {
                    qa_et_reply.setText(it)
                }
            }
        }
    }

}