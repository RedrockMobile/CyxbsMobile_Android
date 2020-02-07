package com.mredrock.cyxbs.mine.page.comment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment

/**
 * Created by roger on 2019/12/5
 */
class CommentFragment : BaseRVFragment<Comment>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CommentViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
//        val comment = Comment("张树洞", "房子涨了，我劝你赶紧买房子涨了，我劝你赶紧买房子涨了，我劝你赶紧买房子涨了，我劝你赶紧买")
//        val list: MutableList<Comment> = mutableListOf()
//        for (i in 1..20) {
//            list.add(comment)
//        }
//        viewModel.fakeComments.postValue(list)
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })
        viewModel.fakeComments.observe(this, Observer {
            loadIntoRv(it)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        getFooter().showLoading()
    }

    /**
     * 添加数据到recyclerView中，并显示没有更多
     */
    private fun loadIntoRv(list: List<Comment>?) {
        if (list == null) {
            return
        }
        if (list.isEmpty()) {
            getFooter().showNoMore()
        } else {
            addData(list)
        }
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_comment
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Comment) {
//        holder.itemView.mine_comment_tv_at_who.text = data.repsonseTo
//        holder.itemView.mine_comment_tv_content.text = data.content
    }


    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }
}