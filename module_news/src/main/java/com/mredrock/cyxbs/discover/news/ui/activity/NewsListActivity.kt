package com.mredrock.cyxbs.discover.news.ui.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_NEWS
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.news.R
import com.mredrock.cyxbs.discover.news.ui.adapter.NewsAdapter
import com.mredrock.cyxbs.discover.news.viewmodel.NewsListViewModel

/**
 * @author zixuan
 * 2019/11/20
 */
@Route(path = DISCOVER_NEWS)
class NewsListActivity : BaseViewModelActivity<NewsListViewModel>() {
    private val srl_list by R.id.srl_list.view<SwipeRefreshLayout>()
    private val rv_list by R.id.rv_list.view<RecyclerView>()


    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity_list)

        common_toolbar.init(this, "教务新闻")

        viewModel.newsEvent.observe {
            srl_list.isRefreshing = false
            adapter.appendNewsList(it ?: return@observe)
        }

        srl_list.setOnRefreshListener {
            adapter.clear()
            viewModel.clearPages()
            viewModel.loadNewsData()
        }

        adapter = NewsAdapter(viewModel::loadNewsData)
        rv_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL).apply { setDrawable(ContextCompat.getDrawable(baseContext,R.drawable.news_recycler_item_split)!!) })
    }

}
