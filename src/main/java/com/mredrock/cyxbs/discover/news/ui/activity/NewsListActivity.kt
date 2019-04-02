package com.mredrock.cyxbs.discover.news.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_NEWS
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.news.R
import com.mredrock.cyxbs.discover.news.network.ApiService
import com.mredrock.cyxbs.discover.news.ui.adapter.NewsAdapter
import kotlinx.android.synthetic.main.news_activity_list.*

@Route(path = DISCOVER_NEWS)
class NewsListActivity : BaseActivity() {

    override val isFragmentActivity = false

    private val service = ApiGenerator.getApiService(ApiService::class.java)
    private var nextPage = 1

    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity_list)

        common_toolbar.init("教务新闻")

        service.getNewsList(nextPage)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    adapter = NewsAdapter(it.toMutableList())
                    rv_list.adapter = adapter
                    nextPage++
                }
        rv_list.layoutManager = LinearLayoutManager(this)

        srl_list.setOnRefreshListener {
            service.getNewsList(nextPage)
                    .mapOrThrowApiException()
                    .setSchedulers()
                    .doFinally { srl_list.isRefreshing = false }
                    .safeSubscribeBy {
                        adapter.appendNewsList(it)
                        nextPage++
                    }
        }
    }

}
