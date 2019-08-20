package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEditMemorandumBookModel
import com.mredrock.cyxbs.freshman.util.MemorandumManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Create by yuanbing
 * on 2019/8/10
 */
class ActivityEditMemorandumBookModel : BaseModel(), IActivityEditMemorandumBookModel {
    override fun getMemorandumBook(callback: (List<ParseBean>) -> Unit) {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    MemorandumManager.getAll().let {
                        it.reverse()
                        it
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it) }
    }

    override fun deleteMemorandumBook(memorandumBook: List<String>, callback: () -> Unit) {
        Observable.just(memorandumBook)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    for (name in it) {
                        MemorandumManager.remove(name)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback() }
    }
}