package com.mredrock.cyxbs.qa.pages.search.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.pages.search.room.QAHistory
import com.mredrock.cyxbs.qa.pages.search.room.QASearchHistoryDatabase
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers


/**
 * Created by yyfbe, Date on 2020/8/12.
 */
class SearchViewModel : BaseViewModel() {
    val historyFromDB = MutableLiveData<MutableList<QAHistory>>()
    private val qaSearchHistoryDatabase: QASearchHistoryDatabase by lazy { QASearchHistoryDatabase.getDatabase(BaseApp.context) }

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
        val dao = qaSearchHistoryDatabase.getHistoryDao()
        Observable.create(ObservableOnSubscribe<Unit>(function = {
            it.onNext(dao.delete(history))
        })).setSchedulers()
                .flatMap {
                    dao.getHistory().toObservable().setSchedulers()
                }
                .safeSubscribeBy {
                    historyFromDB.value = it
                }
    }

    fun deleteAll() {
        val dao = qaSearchHistoryDatabase.getHistoryDao()
        Observable.create(ObservableOnSubscribe<Unit>(function = {
            it.onNext(dao.deleteAll())
        })).setSchedulers()
                .flatMap {
                    dao.getHistory().toObservable().setSchedulers()
                }
                .safeSubscribeBy {
                    historyFromDB.value = it
                }
    }

    fun update(history: QAHistory) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    qaSearchHistoryDatabase.getHistoryDao()
                            .update(history)
                }.lifeCycle()

    }

    fun insert(history: QAHistory) {
        Observable.just(history)
                .subscribeOn(Schedulers.io())
                .safeSubscribeBy {
                    qaSearchHistoryDatabase.getHistoryDao()
                            .insertHistory(it)
                }.lifeCycle()

    }

}