package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.event.OpenShareQuestionEvent
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_posted.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by roger on 2019/12/1
 */
class AskPostedFm : BaseRVFragment<AskPosted>() {


    private val viewModel by lazy { ViewModelProvider(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAskPostedList()
        viewModel.askPosted.observe(viewLifecycleOwner, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskPosted.observe(viewLifecycleOwner, Observer {
            setState(it)
        })
        viewModel.navigateEvent.observe(viewLifecycleOwner, Observer {
            EventBus.getDefault().postSticky(OpenShareQuestionEvent(it.data))
            ARouter.getInstance().build(QA_ANSWER_LIST).navigation()
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_posted
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskPosted) {
        holder.itemView.mine_ask_posted_tv_title.text = data.title
        holder.itemView.mine_ask_posted_tv_disappear_at.apply {
            text = data.disappearAt.split(" ")[0].replace("-", ".")
        }
        holder.itemView.mine_ask_posted_tv_integral.text = data.integral.toString()
        holder.itemView.mine_ask_posted_tv_description.text = data.description
        holder.itemView.mine_ask_posted_tv_state.text = data.type
        if (data.type == "已解决") {
            holder.itemView.mine_ask_posted_tv_state.background = ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_round_corner_blue, null)
            context?.let {
                holder.itemView.mine_ask_posted_tv_disappear_at.setTextColor( ContextCompat.getColor(it, R.color.common_mine_about_text_color_blue))
            }
        } else {
            holder.itemView.mine_ask_posted_tv_state.background = ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_round_corner_brown, null)
            context?.let {
                holder.itemView.mine_ask_posted_tv_disappear_at.setTextColor(ContextCompat.getColor(it, R.color.common_mine_list_item_tv_disappear_at))
            }
        }
        holder.itemView.setOnClickListener {
            viewModel.getQuestion(data.questionId)
        }
    }


    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanAskPostedPage()
        viewModel.loadAskPostedList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}