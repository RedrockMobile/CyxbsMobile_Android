package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.IS_ANSWER
import com.mredrock.cyxbs.common.config.NAVIGATE_FROM_WHERE
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.event.OpenShareCommentEvent
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_answer_posted.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by roger on 2019/12/3
 */
class AnswerPostedFm : BaseRVFragment<AnswerPosted>() {

    private val viewModel by lazy { ViewModelProvider(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAnswerPostedList()
        viewModel.eventOnAnswerPosted.observe(viewLifecycleOwner, Observer {
            setState(it)
        })
        viewModel.answerPosted.observe(viewLifecycleOwner, Observer {
            setNewData(it)
        })
        viewModel.navigateEvent.observe(viewLifecycleOwner, Observer {
            EventBus.getDefault().postSticky(OpenShareCommentEvent(it.qid.toString(), it.data))
            ARouter.getInstance().build(QA_COMMENT_LIST).withInt(NAVIGATE_FROM_WHERE, IS_ANSWER).navigation()
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_answer_posted
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AnswerPosted) {
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_content).text = data.content
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_disappear_at).text = data.answerTime.split(" ")[0].replace("-", ".")
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_integral).text = data.integral.toString()
        holder.itemView.mine_answer_posted_tv_state.text = data.type
        if (data.type == "已采纳") {
            holder.itemView.mine_answer_posted_tv_state.background = ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_round_corner_blue, null)
            context?.let {
                holder.itemView.mine_answer_posted_tv_disappear_at.setTextColor(ContextCompat.getColor(it, R.color.common_mine_about_text_color_blue))
            }
        } else {
            holder.itemView.mine_answer_posted_tv_state.background = ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_round_corner_brown, null)
            context?.let {
                holder.itemView.mine_answer_posted_tv_disappear_at.setTextColor(ContextCompat.getColor(it, R.color.common_mine_list_item_tv_disappear_at))
            }
        }
        holder.itemView.setOnClickListener {
            viewModel.getAnswer(data.questionId, data.answerId)
        }
    }


    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanAnswerPostedPage()
        viewModel.loadAnswerPostedList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}