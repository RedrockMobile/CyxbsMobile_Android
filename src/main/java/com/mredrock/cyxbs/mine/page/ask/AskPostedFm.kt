package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_posted.view.*

/**
 * Created by roger on 2019/12/1
 */
class AskPostedFm : BaseRVFragment<AskPosted>() {

    private var rvState: RvFooter.State = RvFooter.State.LOADING

    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAskPostedList()
        viewModel.askPosted.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskPosted.observe(this, Observer {
            //由于footer可能还未加载到视图，故不可调用getFooter的方法
            if (it == true) {
                rvState = RvFooter.State.NOMORE
            } else {
                rvState = RvFooter.State.ERROR
            }
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_posted
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (rvState) {
            RvFooter.State.NOMORE -> getFooter().showNoMore()
            RvFooter.State.LOADING -> getFooter().showLoading()
            RvFooter.State.ERROR -> getFooter().showLoadError()
            else -> {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskPosted) {
        holder.itemView.mine_ask_posted_tv_title.text = data.title
        holder.itemView.mine_ask_posted_tv_disappear_at.text = data.disappearAt
        holder.itemView.mine_ask_posted_tv_integral.text = data.integral.toString()
        holder.itemView.mine_ask_posted_tv_description.text = data.description
        holder.itemView.mine_ask_posted_tv_state.text = data.type
    }


    override fun onSwipeLayoutRefresh() {
        rvState = RvFooter.State.LOADING
        viewModel.cleanAskPostedPage()
        viewModel.loadAskPostedList()
        getSwipeLayout().isRefreshing = false
    }
}