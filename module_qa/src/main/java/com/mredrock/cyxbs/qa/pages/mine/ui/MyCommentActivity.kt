package com.mredrock.cyxbs.qa.pages.mine.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.QA_MY_COMMENT
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.ReplyInfo
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.MyCommentRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyCommentViewModel
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.utils.KeyboardController
import kotlinx.android.synthetic.main.qa_activity_dynamic_detail.*
import kotlinx.android.synthetic.main.qa_activity_my_comment.*
import kotlinx.android.synthetic.main.qa_activity_my_comment.qa_btn_my_comment_send
import kotlinx.android.synthetic.main.qa_activity_my_comment.qa_et_my_comment_reply
import kotlinx.android.synthetic.main.qa_activity_my_comment.qa_iv_my_comment_select_pic
import kotlinx.android.synthetic.main.qa_common_toolbar.*

@Route(path = QA_MY_COMMENT)
class MyCommentActivity : BaseViewModelActivity<MyCommentViewModel>() {

    private lateinit var myCommentRvAdapter: MyCommentRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private var curReplyComment: Comment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_comment)
        initView()
        initObserve()
        initClick()
    }

    private fun initObserve() {

        //观察cwList是否变化
        viewModel.cwList.observe {
            myCommentRvAdapter.submitList(it)
        }

        //观察网络状态
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        //观察网络请求返回结果
        viewModel.initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    qa_swl_my_comment.isRefreshing = true
                    (qa_rv_my_comment.adapter as? RvAdapterWrapper)?.apply {
                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_swl_my_comment.isRefreshing = false
                }
                else -> {
                    qa_swl_my_comment.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    private fun initView() {
        //初始化toolbar
        qa_tv_toolbar_title.text = "评论回复"
        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
        qa_tb_my_comment.setBackgroundColor(ContextCompat.getColor(this, R.color.qa_ignore_item_background_color))

        //初始化三个Adapter
        myCommentRvAdapter = MyCommentRvAdapter(this, viewModel)
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_my_comment_no_reply))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = myCommentRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )

        qa_rv_my_comment.adapter = adapterWrapper
        qa_rv_my_comment.layoutManager = LinearLayoutManager(this)
        //设置刷新
        qa_swl_my_comment.setOnRefreshListener {
            viewModel.invalidateCWList()
        }
    }

    //传递评论的id进来，展开键盘进行回复
    fun showKeyboard(comment: Comment) {
        KeyboardController.showInputKeyboard(this, qa_et_my_comment_reply)
        curReplyComment = comment
    }

    private fun initClick() {
        qa_btn_my_comment_send.setOnClickListener {
            curReplyComment?.let {
                viewModel.reply(it, qa_et_my_comment_reply.text.toString()) {
                    BaseApp.context.toast("评论成功")
                    qa_cl_my_comment_reply.visibility = View.GONE
                    KeyboardController.hideInputKeyboard(this, qa_et_my_comment_reply)
                }
            }
        }

        qa_iv_my_comment_select_pic.setOnSingleClickListener {
            curReplyComment?.let { comment ->
                Intent(this, QuizActivity::class.java).apply {
                    putExtra("isComment", "1")
                    putExtra("postId", comment.postId)
                    putExtra("replyId", comment.replyId)
                    putExtra("commentContent", qa_et_my_comment_reply.text.toString())
                    putExtra("replyNickname", comment.fromNickname)
                    startActivity(this)
                }
            }
        }
        qa_et_my_comment_reply.addTextChangedListener {
            it?.apply {
                qa_btn_my_comment_send.background =
                        if (length == 0) {
                            ContextCompat.getDrawable(this@MyCommentActivity, R.drawable.qa_shape_send_dynamic_btn_grey_background)
                        } else {
                            ContextCompat.getDrawable(this@MyCommentActivity, R.drawable.qa_shape_send_dynamic_btn_blue_background)
                        }
            }
        }
    }
}