package com.mredrock.cyxbs.mine.page.aboutme

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.RelateMeItem
import com.mredrock.cyxbs.mine.util.log
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_fragment_base_rv.*
import kotlinx.android.synthetic.main.mine_item_relate_me_rv.view.*
import kotlinx.android.synthetic.main.mine_layout_me_empty_data.view.*

/**
 * Created by zia on 2018/9/13.
 */
class AboutMeFragment : BaseRVFragment<RelateMeItem>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AboutMeViewModel::class.java) }
    private val placeHolder by lazy {
        val view = layoutInflater.inflate(R.layout.mine_layout_me_empty_data
                , mine_fragment_base_placeholder, false)
        view.mine_draft_empty_hint.text = getString(R.string.mine_relate_me_empty_hint)
        view
    }


    private var type = ALL

    override fun getItemLayout(): Int {
        return R.layout.mine_item_relate_me_rv
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initObserver()
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })

        viewModel.dataEvent.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            if (it.isEmpty() && getFooter().state == RvFooter.State.LOADING) {
                getFooter().showNoMore()
                return@Observer
            } else {
                hidePlaceHolder()
                log("load type:$type,data:$it")
                addData(it)
            }
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        getFooter().showLoading()
        viewModel.loadData(type)
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: RelateMeItem) {
        holder.itemView.mine_aboutme_item_avatar.setImageFromUrl(data.photoThumbnailSrc)
        holder.itemView.mine_aboutme_item_nickname.text = data.nickname
        holder.itemView.mine_aboutme_item_time.text = data.createdAt
        holder.itemView.mine_aboutme_item_answerContent.text = data.answerContent
        if(data.content == ""){
            holder.itemView.mine_aboutme_item_content.setPadding(0,0,0,0)
        } else {
            holder.itemView.mine_aboutme_item_content.text = data.content
        }
        when (data.type) {
            REMARK.toString() -> {
                holder.itemView.mine_aboutme_item_type.text = "评论"
            }
            STAR.toString() -> {
                holder.itemView.mine_aboutme_item_type.text = "赞"
            }
        }
    }

    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }

    fun setType(type: Int) {
        this.type = type
    }

    companion object {
        const val ALL = 3
        const val REMARK = 2
        const val STAR = 1
    }

}