package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.QA_DYNAMIC_DETAIL
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
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
@Route(path = QA_DYNAMIC_DETAIL)
class DynamicDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {
    companion object {

        // 有共享元素动画的启动
        fun activityStart(page: Any?, dynamicItem: View, data: Dynamic) {

            page.apply {
                var activity: Activity? = null
                var fragment: Fragment? = null
                if (page is Fragment) {
                    fragment = page
                    activity = page.activity
                } else if (page is Activity) {
                    activity = page
                }
                activity?.let { it ->
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, dynamicItem, "dynamicItem")
                    val intent = Intent(it, DynamicDetailActivity::class.java)
                    intent.putExtra("dynamic", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 300 }
                    if (fragment != null) {
                        fragment.startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST, opt.toBundle())
                    } else {
                        it.startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST, opt.toBundle())
                    }
                }
            }
        }

        // 根据postId启动
        fun activityStart(page: Any?, postId: String) {

            var activity: Activity? = null
            if (page is Fragment) {
                activity = page.activity
            } else if (page is Activity) {
                activity = page
            }

            activity.apply {
                activity?.let {
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    intent.putExtra("post_id", postId)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 300 }
                    it.startActivityForResult(intent, DYNAMIC_DETAIL_REQUEST)
                }
            }
        }

        const val DYNAMIC_DELETE = "0"
        const val COMMENT_DELETE = "1"
    }

    private val animatorDuration = 300L

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }

    private val footerRvAdapter = FooterRvAdapter { refreshCommentList() }

    private val behavior by lazy { AppBarLayout.ScrollingViewBehavior() }

//    override val isFragmentActivity = false

    private var mTencent: Tencent? = null

    private var haveShareItem = false

    private var dynamic: Dynamic? = null

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()


    private fun refreshCommentList() {
        viewModel.dynamic.value?.let {
            viewModel.refreshCommentList(it.postId, "-1")
        }
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
                if (viewModel.dynamic.value?.isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            viewModel.deleteId(comment.commentId, COMMENT_DELETE)
                        }
                    }
                }
                if (!comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog(this).apply {
                            show { reportContent ->
                                viewModel.report(comment.commentId, reportContent, CommentConfig.REPORT_COMMENT_MODEL)
                            }
                        }.show()
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
                // 如果总动态是自己发的，或者该评论是自己的，则可以删除（可以控评）
                if (viewModel.dynamic.value?.isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            viewModel.deleteId(comment.commentId, COMMENT_DELETE)
                        }
                    }
                }
                // 如果回复不是自己的，就可以举报
                if (!comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog(this).apply {
                            show { reportContent ->
                                viewModel.report(comment.commentId, reportContent, CommentConfig.REPORT_COMMENT_MODEL)
                            }
                        }.show()
                    }
                }
                optionPopWindow.show(itemView, OptionalPopWindow.AlignMode.CENTER, 0)

            },
            onMoreReplyClickEvent = { commentId -> ReplyDetailActivity.activityStart(this, commentId, null) }
    )


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_dynamic_detail)

        dynamic = intent.getParcelableExtra("dynamic")

        viewModel.position = -1

        intent.extras?.apply {
            if (!getBoolean("isFromReceive")) return@apply
            val postId = getString("id")
            intent.putExtra("post_id", postId)
        }

        // 设置进入动画
        window.enterTransition = Slide(Gravity.END).apply { duration = animatorDuration }
        // 设置标题
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)
        // 注册
        mTencent = Tencent.createInstance(CommentConfig.APP_ID, this)

        // 判断是否有共享元素
        haveShareItem = intent.getParcelableExtra<Dynamic>("dynamic") != null

        // 清空原来的评论显示
        viewModel.commentList.value = listOf()

        initChangeColorAnimator("#1D1D1D", "#000000")
        initObserve()
        initDynamic()
        initReplyList()
        initOnClickListener()

    }

    private fun initChangeColorAnimator(startColor: String, endColor: String) {
        val isDarkMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (haveShareItem) {
            if (isDarkMode) {//如果是深色模式
                //因为视觉图颜色不一样的缘故，这里要在转场动画的同时添加一个变色动画
                val colorStart = Color.parseColor(startColor)
                val colorFinal = Color.parseColor(endColor)
                val va = ValueAnimator.ofInt(colorStart, colorFinal)
                va.setEvaluator(ArgbEvaluator())
                va.duration = animatorDuration
                va.addUpdateListener { vav ->
                    val gd = qa_ctl_dynamic.background as GradientDrawable
                    gd.setColor(vav.animatedValue as Int)
                }
                va.start()
            }
        } else if (isDarkMode) {
            //如果是深色模式，就直接变色，不加动画
            val gd = qa_ctl_dynamic.background as GradientDrawable
            gd.setColor(Color.parseColor(endColor))
        }

        if (viewModel.dynamic.value?.postId == "0") {
            context.toast("帖子不存在或已删除")
            return
        }
    }

    private fun initOnClickListener() {
        qa_iv_my_comment_select_pic.setOnSingleClickListener {
            Intent(this, QuizActivity::class.java).apply {
                putExtra("isComment", "1")
                putExtra("postId", viewModel.dynamic.value?.postId)
                putExtra("replyId", viewModel.replyInfo.value?.replyId ?: "")
                putExtra("commentContent", qa_et_my_comment_reply.text.toString())
                putExtra("replyNickname", viewModel.replyInfo.value?.nickname ?: "")
                startActivityForResult(this, RequestResultCode.RELEASE_COMMENT_ACTIVITY_REQUEST)
            }
        }

        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }

        qa_btn_my_comment_send.setOnSingleClickListener {
            viewModel.dynamic.value?.let {
                viewModel.releaseComment(it.postId, qa_et_my_comment_reply.text.toString())
            }
        }
        qa_iv_dynamic_comment_count.setOnSingleClickListener {
            KeyboardController.showInputKeyboard(this, qa_et_my_comment_reply)
            viewModel.replyInfo.value = ReplyInfo("", "", "")
        }
        qa_coordinatorlayout.onReplyCancelEvent = {
            viewModel.replyInfo.value = ReplyInfo("", "", "")
            KeyboardController.hideInputKeyboard(this, qa_et_my_comment_reply)
        }
    }

    private fun initObserve() {
        viewModel.commentList.observe(this, Observer {
            if (it != null) {
                commentListRvAdapter.refreshData(it)
                if (viewModel.position != -1) {

                    qa_rv_comment_list.smoothScrollToPosition(viewModel.position)
                    Log.e("sandyzhang======", viewModel.position.toString())
                    viewModel.position = -1
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
                    KeyboardController.showInputKeyboard(this@DynamicDetailActivity, qa_et_my_comment_reply)
                    ReplyPopWindow.with(this@DynamicDetailActivity)
                    ReplyPopWindow.setReplyName(it.nickname, it.content)
                    ReplyPopWindow.setOnClickEvent {
                        viewModel.replyInfo.value = ReplyInfo("", "", "")
                    }
                    ReplyPopWindow.show(qa_et_my_comment_reply, ReplyPopWindow.AlignMode.LEFT, this@DynamicDetailActivity.dp2px(6f))
                } else {
                    qa_coordinatorlayout.isReplyEdit = false
                    if (ReplyPopWindow.isShowing()) {
                        ReplyPopWindow.dismiss()
                    }
                }

            }
        }
        viewModel.commentReleaseResult.observe {
            viewModel.replyInfo.value = ReplyInfo("", "", "")
            qa_et_my_comment_reply.setText("")
            KeyboardController.hideInputKeyboard(this, qa_et_my_comment_reply)

            viewModel.dynamic.value?.let { dynamic ->
                viewModel.refreshCommentList(dynamic.postId, (it?.commentId
                        ?: -1).toString())
            }
        }
        viewModel.deleteDynamic.observe {
            // 通知主页面刷新
            setResult(NEED_REFRESH_RESULT)
            finish()
        }
        viewModel.dynamic.observe {
            refreshDynamic()
        }

        qa_et_my_comment_reply.addTextChangedListener {
            it?.apply {
                qa_btn_my_comment_send.background =
                        if (length == 0) {
                            ContextCompat.getDrawable(this@DynamicDetailActivity, R.drawable.qa_shape_send_dynamic_btn_grey_background)
                        } else {
                            ContextCompat.getDrawable(this@DynamicDetailActivity, R.drawable.qa_shape_send_dynamic_btn_blue_background)
                        }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //修改详情的背景颜色
        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            val gd = qa_ctl_dynamic.background as GradientDrawable
            gd.setColor(Color.parseColor("#1D1D1D"))
        }
    }

    private fun initDynamic() {


        // 设置behavior
        val layoutParams = qa_ll_reply.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = behavior


        if (dynamic == null) {
            dynamic = Dynamic().apply {
                postId = intent.getStringExtra("post_id")
                if (postId == "0") {
                    context.toast("帖子不存在或已删除")
                    return
                }
            }
        }
        dynamic?.let {
            viewModel.dynamic.value = it
        }

        qa_iv_dynamic_share.setOnSingleClickListener { view ->
            viewModel.dynamic.value?.let { dynamic ->
                ShareDialog(view.context).apply {
                    val url = "${CommentConfig.SHARE_URL}dynamic?id=${dynamic.postId}"
                    initView(onCancelListener = View.OnClickListener {
                        dismiss()
                    }, qqshare = View.OnClickListener {
                        val pic = if (dynamic.pics.isNullOrEmpty()) "" else dynamic.pics[0]
                        mTencent?.let { it1 -> ShareUtils.qqShare(it1, this@DynamicDetailActivity, dynamic.topic, dynamic.content, url, pic) }
                    }, qqZoneShare = View.OnClickListener {
                        mTencent?.let { it1 -> ShareUtils.qqQzoneShare(it1, this@DynamicDetailActivity, dynamic.topic, dynamic.content, url, ArrayList(dynamic.pics)) }

                    }, weChatShare = View.OnClickListener {
                        CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()

                    }, friendShipCircle = View.OnClickListener {
                        CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()

                    }, copylink = View.OnClickListener {
                        ClipboardController.copyText(this@DynamicDetailActivity, url)
                    })
                }.show()
            }
        }

        qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {

            // 消除回复弹窗
            viewModel.replyInfo.value = ReplyInfo("", "", "")

            val optionPopWindow = OptionalPopWindow.Builder().with(this)
                    .addOptionAndCallback(CommentConfig.REPLY) {
                        KeyboardController.showInputKeyboard(this, qa_et_my_comment_reply)
                        viewModel.replyInfo.value = ReplyInfo("", "", "")
                    }.addOptionAndCallback(CommentConfig.COPY) {
                        viewModel.dynamic.value?.let {
                            ClipboardController.copyText(this, it.content)
                        }
                    }
            if (viewModel.dynamic.value?.isSelf == 1) { // 如果帖子是自己的，则可以删除
                optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                    QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_dynamic_text), {}) {
                        viewModel.dynamic.value?.let { dynamic ->
                            viewModel.deleteId(dynamic.postId, DYNAMIC_DELETE)
                        }
                    }
                }
            } else { // 如果是别人的帖子，则可以举报
                optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                    QaReportDialog(this).apply {
                        show { reportContent ->
                            viewModel.dynamic.value?.let { dynamic ->
                                viewModel.report(dynamic.postId, reportContent, CommentConfig.REPORT_DYNAMIC_MODEL)
                            }
                        }
                    }.show()
                }
            }
            optionPopWindow.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
        }
        dynamic?.let {
            qa_iv_dynamic_praise_count_image.registerLikeView(it.postId, CommentConfig.PRAISE_MODEL_DYNAMIC, it.isPraised, it.praiseCount)
        }
        qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
            qa_iv_dynamic_praise_count_image.click()
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

    @SuppressLint("SetTextI18n")
    private fun refreshDynamic() {
        qa_iv_dynamic_avatar.setAvatarImageFromUrl(viewModel.dynamic.value?.avatar)
        qa_tv_dynamic_topic.text = "# " + viewModel.dynamic.value?.topic
        qa_tv_dynamic_nickname.text = viewModel.dynamic.value?.nickName
        qa_tv_dynamic_content.setContent(viewModel.dynamic.value?.content)
        qa_tv_dynamic_comment_count.text = viewModel.dynamic.value?.commentCount.toString()
        viewModel.dynamic.value?.let {
            qa_iv_dynamic_praise_count_image.registerLikeView(it.postId, CommentConfig.PRAISE_MODEL_DYNAMIC, it.isPraised, it.praiseCount)
        }
        viewModel.dynamic.value?.let {
            qa_tv_dynamic_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), it.publishTime * 1000)
        }
        if (viewModel.dynamic.value?.pics.isNullOrEmpty())
            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
        else {
            viewModel.dynamic.value?.pics?.apply {
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
            viewModel.dynamic.value?.let { dynamic ->
                ViewImageActivity.activityStart(this, dynamic.pics.map { it }.toTypedArray(), index)
            }
        }
        val data = Intent()
        data.putExtra("refresh_dynamic", viewModel.dynamic.value)
        setResult(-1, data)
    }

    override fun onBackPressed() {
        if (ReplyPopWindow.isShowing()) {
            ReplyPopWindow.dismiss()
            return
        }

        if (haveShareItem) {
            initChangeColorAnimator("#000000", "#1D1D1D")
        }
        window.returnTransition = Slide(Gravity.END).apply { duration = animatorDuration }
        super.onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // 从动态详细返回
            RequestResultCode.RELEASE_COMMENT_ACTIVITY_REQUEST -> {
                if (resultCode == NEED_REFRESH_RESULT) {
                    // 需要刷新 则 刷新显示动态
                    viewModel.dynamic.value?.let {
                        viewModel.refreshCommentList(it.postId, "-1")
                    }
                    viewModel.replyInfo.value = ReplyInfo("", "", "")
                }
                data?.getStringExtra("text")?.let {
                    qa_et_my_comment_reply.setText(it)
                }
            }
        }
    }
}