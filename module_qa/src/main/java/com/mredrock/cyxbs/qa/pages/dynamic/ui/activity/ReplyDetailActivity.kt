package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.ReplyInfo
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.ReplyDetailAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.ui.widget.QaDialog
import com.mredrock.cyxbs.qa.ui.widget.QaReportDialog
import com.mredrock.cyxbs.qa.utils.ClipboardController
import io.reactivex.Observable
import kotlinx.android.synthetic.main.qa_activity_reply_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlin.math.max


/**
 *@author zhangzhe
 *@date 2020/12/15
 *@description 复用DynamicDetailViewModel的详细界面
 */

class ReplyDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {
    // 要展示的回复的id
    private var commentId: String = "-1"

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()

    // 增加回复的header
    private var itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(0, 0, 0, dp2px(40f))
            } else {
                outRect.set(0, 0, 0, 0)
            }
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val height = dp2px(40f) // header高度
            val topY = max((parent.layoutManager?.findViewByPosition(1)?.top?.toFloat()
                    ?: 0f) - height, 0f)

            c.drawRect(Rect(0, topY.toInt(), parent.width, topY.toInt() + height), Paint().apply {
                color = resources.getColor(R.color.qa_reply_detail_header_background_color)
                isAntiAlias = true
            })
            c.drawText("回复", dp2px(12f).toFloat(), topY + dp2px(26f), Paint().apply {
                textSize = sp(16).toFloat()
                isAntiAlias = true
                color = resources.getColor(R.color.common_level_one_font_color)
            })
        }
    }

    /**
     * 筛选条件：只显示：commentId或者replyId == replyIdScreen 的贴子
     */
    private var replyIdScreen: String? = null

    companion object {

        fun activityStart(activity: Activity, cId: String, replyIdScreen: String?) {
            activity.apply {
                window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                startActivityForResult(
                        Intent(this, ReplyDetailActivity::class.java).apply {
                            putExtra("commentId", cId)
                            replyIdScreen?.let { putExtra("replyIdScreen", it) }
                        },
                        RequestResultCode.REPLY_DETAIL_REQUEST
                )
            }
        }
    }

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }

    private val footerRvAdapter = FooterRvAdapter { refresh() }

    private var replyDetailAdapter: ReplyDetailAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_reply_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        replyIdScreen = intent.getStringExtra("replyIdScreen")
        commentId = intent.getStringExtra("commentId")

        initToolbar()
        initObserver()
        initReplyList()

    }

    private fun initObserver() {
        /**
         * 从整个评论列表中根据{@param replyId}找到当前评论的回复。
         */
        viewModel.commentList.observe(this, Observer { value ->

            Observable.create<List<Comment>> {
                var dataList: List<Comment>? = null
                value.forEach {
                    if (it.commentId == commentId) {
                        dataList = it.replyList.toMutableList().filter { comment ->
                            if (replyIdScreen.isNullOrEmpty()) {
                                true
                            } else {
                                comment.commentId == replyIdScreen || comment.replyId == replyIdScreen
                            }
                        }.sortedBy { comment ->
                            comment.publishTime
                        }
                    }
                }
                dataList?.let { it1 -> it.onNext(it1) }
            }
                    .setSchedulers()
                    .safeSubscribeBy {
                        it?.toMutableList()?.let { it1 -> replyDetailAdapter?.refreshData(it1) }

                        qa_reply_detail_swipe_refresh.isRefreshing = false

                        qa_reply_detail_rv_reply_list.removeItemDecoration(itemDecoration)
                        if (!replyIdScreen.isNullOrEmpty() && (it?.size
                                        ?: 0) > 1 && it?.get(0)?.commentId == replyIdScreen) {
                            qa_reply_detail_rv_reply_list.addItemDecoration(itemDecoration)
                        }

                    }

        })
    }

    private fun initReplyList() {

        qa_reply_detail_swipe_refresh.setOnRefreshListener {
            refresh()
        }

        replyDetailAdapter = ReplyDetailAdapter(
                isReplyDetail = !replyIdScreen.isNullOrEmpty(),
                onReplyInnerClickEvent = { comment ->
                    startActivity(Intent(this, DynamicDetailActivity::class.java))
                    viewModel.replyInfo.value = ReplyInfo(comment.nickName, comment.content, comment.commentId)
                },
                onReplyInnerLongClickEvent = { comment, itemView ->
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
                                viewModel.deleteId(comment.commentId, DynamicDetailActivity.COMMENT_DELETE)
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
                onReplyMoreDetailClickEvent = { replyIdScreen ->
                    activityStart(this, commentId, replyIdScreen)

                }
        )

        qa_reply_detail_rv_reply_list.apply {
            layoutManager = LinearLayoutManager(context)

            val adapterWrapper = RvAdapterWrapper(
                    normalAdapter = replyDetailAdapter!!,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
            adapter = adapterWrapper
        }

    }

    private fun initToolbar() {
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_reply_detail_title_text)
        qa_include_toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.qa_reply_inner_background_color))
        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
    }


    fun refresh() {
        viewModel.refreshCommentList(viewModel.dynamic.value?.postId ?: "-1", "-1")
    }
}