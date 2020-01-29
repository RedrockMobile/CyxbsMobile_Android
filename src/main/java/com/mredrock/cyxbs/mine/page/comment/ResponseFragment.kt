package com.mredrock.cyxbs.mine.page.comment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.RelateMeItem
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_fragment_base_rv.*
import kotlinx.android.synthetic.main.mine_layout_me_empty_data.view.*
import kotlinx.android.synthetic.main.mine_list_item_comment_repsonse.view.*

/**
 * Created by roger on 2019/12/5
 */
class ResponseFragment : BaseRVFragment<RelateMeItem>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CommentViewModel::class.java) }
    private val placeHolder by lazy {
        val view = layoutInflater.inflate(R.layout.mine_layout_me_empty_data
                , mine_fragment_base_placeholder, false)
        view.mine_draft_empty_hint.text = getString(R.string.mine_relate_me_empty_hint)
        view
    }


    private var type = REMARK

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_repsonse
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

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: RelateMeItem) {
        holder.itemView.mine_comment_circleimageview_avatar.setImageFromUrl(data.photoThumbnailSrc)
        holder.itemView.mine_comment_tv_nickname.text = data.nickname
        holder.itemView.mine_comment_tv_response.text = data.answerContent
    }

    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }

    companion object {
        const val ALL = 3
        const val REMARK = 2
        const val STAR = 1
    }

}