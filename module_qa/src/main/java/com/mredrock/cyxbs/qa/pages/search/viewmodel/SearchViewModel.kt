package com.mredrock.cyxbs.qa.pages.search.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.bean.QAHistory
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.search.room.QASearchHistoryDatabase
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers


/**
 * Created by yyfbe, Date on 2020/8/12.
 */
class SearchViewModel : BaseViewModel() {
    //只用于fragment加载时，首次请求，相当于从数据库拿出来的缓存
    val historyFromDB = MutableLiveData<MutableList<QAHistory>>()
    private val qaSearchHistoryDatabase: QASearchHistoryDatabase by lazy { QASearchHistoryDatabase.getDatabase(BaseApp.context) }
    val searchHotWords = MutableLiveData<List<String>>()
    fun getHistoryFromDB() {
        qaSearchHistoryDatabase.getHistoryDao()
                .getHistory()
                .toObservable()
                .setSchedulers()
                .safeSubscribeBy {
                    historyFromDB.value = it
                }
    }

    fun delete(history: QAHistory) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    qaSearchHistoryDatabase.getHistoryDao()
                            .delete(history)
                }.lifeCycle()
    }

    fun deleteAll() {
        val dao = qaSearchHistoryDatabase.getHistoryDao()
        Observable.create(ObservableOnSubscribe<Unit>(function = {
            it.onNext(dao.deleteAll())
        })).setSchedulers()
                .safeSubscribeBy {
                    historyFromDB.value = mutableListOf()
                }
    }

    fun update(history: QAHistory) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    qaSearchHistoryDatabase.getHistoryDao().update(history)
                }.lifeCycle()

    }

    fun insert(history: QAHistory) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    qaSearchHistoryDatabase.getHistoryDao().insertHistory(it)
                }.lifeCycle()

    }

    fun getSearchHotWord() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getSearchHotWord()
                .setSchedulers()
                .safeSubscribeBy {
                    if (it.status == 200) {
                        LogUtils.d("zt","获取热词成功")
                        searchHotWords.value = it.data.hotWords
                    }
                }
    }
}