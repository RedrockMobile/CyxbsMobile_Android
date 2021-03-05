package com.mredrock.cyxbs.qa.pages.mine.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.SimpleRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyCommentViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyCommentViewModel
import kotlinx.android.synthetic.main.qa_recycler_item_my_comment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyCommentActivity : BaseViewModelActivity<MyCommentViewModel>() {

    private lateinit var rvAdapter: SimpleRvAdapter<CommentWrapper>
    private val cwList = ArrayList<CommentWrapper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_comment)
    }

    private fun initView(){
        rvAdapter = SimpleRvAdapter(
                MyCommentViewHolder::class.java,
                R.layout.qa_recycler_item_my_comment,
                cwList
        ){ viewHolder, position ->
            val comment = cwList[position].comment
            val from = cwList[position].from
            val dateFormat = SimpleDateFormat("回复了你的评论      yyyy.mm.dd  hh:mm", Locale.getDefault())
            //TODO: 超过24小时不再显示时间
            viewHolder.itemView.apply {
                qa_tv_my_comment_item_nickname.text = comment.nickName
                qa_tv_my_comment_from.text = from
                qa_tv_my_comment_item_tip_and_date.text = dateFormat.format(Date(comment.publishTime))
                qa_tv_my_comment_item_comment.text = comment.content
            }
        }
    }
}