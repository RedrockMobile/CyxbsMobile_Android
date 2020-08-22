package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
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
    protected val database: HistoryDatabase by lazy { HistoryDatabase.getDatabase(BaseApp.context) }
    abstract fun getPerson(str: String, fromHistory: Boolean? = false)

    abstract fun getHistory()

    protected fun getHistoryInternal(type: Int) {
        database.getHistoryDao()
                .getHistory(type)
                .toObservable()
                .setSchedulers()
                .safeSubscribeBy {
                    mHistory.value = it
                }.lifeCycle()
    }

    protected fun addHistoryInternal(history: History) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    database.getHistoryDao().insertHistory(it)
                }.lifeCycle()
        mHistory.value?.add(0, history)
        mHistory.value = mHistory.value
    }

    abstract fun addHistory(history: History)
}
