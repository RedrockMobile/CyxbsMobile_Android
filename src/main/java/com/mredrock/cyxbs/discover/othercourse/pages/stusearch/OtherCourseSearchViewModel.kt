package com.mredrock.cyxbs.discover.othercourse.pages.stusearch

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
    val mHistory = MutableLiveData<List<History>>()
    protected val database: HistoryDatabase by lazy { HistoryDatabase.getDatabase(BaseApp.context) }
    abstract fun getPerson(str: String)

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
                    database.getHistoryDao().insertHistory(history)
                }.lifeCycle()
    }

    abstract fun addHistory(history: History)

    companion object Factory {
        fun <T : OtherCourseSearchViewModel> create(type: Int): Class<T> {
            return when (type) {
                0 -> OtherCourseStudentSearchViewModel::class.java as Class<T>
                1 -> OtherCourseTeacherSearchViewModel::class.java as Class<T>
                else -> throw IllegalStateException()
            }
        }
    }
}
