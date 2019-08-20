package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityAddMemorandumBookModel
import com.mredrock.cyxbs.freshman.util.MemorandumManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Create by yuanbing
 * on 2019/8/10
 */
class ActivityAddMemorandumBookModel : BaseModel(), IActivityAddMemorandumBookModel {
    override fun saveMemorandumBook(name: String, callback: () -> Unit) {
        Observable.just(name)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { MemorandumManager.add(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback() }
    }
}