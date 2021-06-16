package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.History
import com.mredrock.cyxbs.discover.othercourse.room.HistoryDatabase
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

abstract class OtherCourseSearchViewModel : BaseViewModel() {
    var mList = MutableLiveData<List<Person>>()
    var mListFromHistory = MutableLiveData<List<Person>>()
    val mHistory = MutableLiveData<MutableList<History>>()
    //当前搜索对应的history的id
    var curHistoryId = -1
    protected val database: HistoryDatabase by lazy { HistoryDatabase.getDatabase(BaseApp.context) }
    abstract fun getPerson(str: String, fromHistory: Boolean? = false)

    abstract fun getHistory()

    protected fun getHistoryInternal(type: Int) {
        Observable.create<MutableList<History>> { emitter ->
            database.getHistoryDao()
                    .getHistory(type)
                    .toObservable()
                    .safeSubscribeBy(
                            onError = { emitter.onError(it) },
                            onNext = { emitter.onNext(it) }
                    )
        }.setSchedulers()
                .safeSubscribeBy {
                    mHistory.value = it
                }

    }

    protected fun addHistoryInternal(history: History, onAddFinished: () -> Unit) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    curHistoryId = database.getHistoryDao().insertHistory(it).toInt()
                    onAddFinished.invoke()
                }.lifeCycle()
        mHistory.value?.add(0, history)
        mHistory.value = mHistory.value
    }

    fun deleteHistory(id: Int) {
        Observable.just(id)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    database.getHistoryDao().deleteHistory(it)
                }.lifeCycle()
    }

    //需要在历史记录添加进去之后再跳转activity，所以添加了onAddFinished回调
    abstract fun addHistory(history: History, onAddFinished: ()->Unit)
}
