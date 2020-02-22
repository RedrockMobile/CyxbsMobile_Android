package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.config.QUESTION_ID
import com.mredrock.cyxbs.common.config.QUESTION_REQUEST_BODY_DATA
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_posted.view.*
import org.jetbrains.anko.textColor

/**
 * Created by roger on 2019/12/1
 */
class AskPostedFm : BaseRVFragment<AskPosted>() {


    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAskPostedList()
        viewModel.askPosted.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskPosted.observe(this, Observer {
            if (it == true) {
                getFooter().showNoMore()
            } else {
                getFooter().showLoadError()
            }
        })
        viewModel.navigateEvent.observe(this, Observer {
            val bundle = Bundle()
            bundle.putInt(QUESTION_ID, it.id)
            bundle.putString(QUESTION_REQUEST_BODY_DATA, it.data)
            ARouter.getInstance().build(QA_ANSWER_LIST).with(bundle).navigation()
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_posted
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        //通过footer来判断是否继续加载
        if (getFooter().state == RvFooter.State.LOADING) {
            viewModel.loadAskPostedList()
        }
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
                holder.itemView.mine_ask_posted_tv_disappear_at.textColor = ContextCompat.getColor(it, R.color.mine_about_text_color_blue)
            }
        } else {
            holder.itemView.mine_ask_posted_tv_state.background = ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_round_corner_brown, null)
            context?.let {
                holder.itemView.mine_ask_posted_tv_disappear_at.textColor = ContextCompat.getColor(it, R.color.mine_list_item_tv_disappear_at)
            }
        }
        holder.itemView.setOnClickListener {
            viewModel.getQuestion(data.questionId)
        }
    }


    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanAskPostedPage()
        viewModel.loadAskPostedList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}