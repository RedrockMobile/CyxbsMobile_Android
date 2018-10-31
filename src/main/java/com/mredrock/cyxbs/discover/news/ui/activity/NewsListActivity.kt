package com.mredrock.cyxbs.news.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_NEWS
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.news.R
import com.mredrock.cyxbs.news.network.ApiService
import com.mredrock.cyxbs.news.ui.adapter.NewsAdapter
import kotlinx.android.synthetic.main.news_activity_list.*

@Route(path = DISCOVER_NEWS)
class NewsListActivity : BaseActivity() {

    override val isFragmentActivity = false

    private val service = ApiGenerator.getApiService(ApiService::class.java)
    /*           模拟数据 object : ApiService{
                    override fun getNewsList(page: Int) = Observable.create<RedrockApiWrapper<List<NewsListItem>>> {
                        val g = Gson()
                        it.onNext(RedrockApiWrapper(List<NewsListItem>(5) { _ ->
                            g.fromJson("{\n" +
                                    "               \"totalCount\": 2676,\n" +
                                    "               \"fileId\": 6014,\n" +
                                    "               \"dirId\": \"0001\",\n" +
                                    "               \"title\": \"关于2018-2019学年第一学期学籍异动学生补、改选课的通知\",\n" +
                                    "               \"pubId\": \"060408    \",\n" +
                                    "               \"pubTime\": 2018年9月21日\",\n" +
                                    "               \"pubIp\": \"172.22.80.110       \",\n" +
                                    "               \"readCount\": 37,\n" +
                                    "               \"dirName\": \"最新通知                                \",\n" +
                                    "               \"teaName\": \"高运玲\",\n" +
                                    "               \"days\": 0\n" +
                                    "           }", NewsListItem::class.java)
                        }).apply {
                            status = 200
                        })
                    }

                    override fun getNewsDetails(id: Int) = Observable.create<RedrockApiWrapper<NewsDetails>> {}
               }*/
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
