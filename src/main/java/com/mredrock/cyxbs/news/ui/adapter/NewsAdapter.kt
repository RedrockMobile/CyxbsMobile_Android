package com.mredrock.cyxbs.news.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.news.R
import com.mredrock.cyxbs.news.bean.NewsListItem
import com.mredrock.cyxbs.news.ui.activity.NewsItemActivity
import kotlinx.android.synthetic.main.news_item_news.view.*
import org.jetbrains.anko.startActivity

/**
 * Author: Hosigus
 * Date: 2018/9/20 15:23
 * Description: com.mredrock.cyxbs.news.ui.adapter
 */
class NewsAdapter(private val newsList: MutableList<NewsListItem>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    fun appendNewsList(newList: List<NewsListItem>) {
        newsList.addAll(newList)
        notifyItemChanged(newsList.size - newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.news_item_news, parent, false)
    )

    override fun getItemCount() = newsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]
        holder.itemView.apply {
            setOnClickListener {
                context.startActivity<NewsItemActivity>("id" to news.fileId, "title" to news.title)
            }
            tv_time.text = news.pubTime
            tv_title.text = news.title
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}