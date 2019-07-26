package com.mredrock.cyxbs.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import io.reactivex.disposables.Disposable

/**
 * Created By jay68 on 2018/8/23.
 */
open class BaseViewModel : ViewModel() {
    val toastEvent: MutableLiveData<Int> by lazy { SingleLiveEvent<Int>() }
    val longToastEvent: MutableLiveData<Int> by lazy { SingleLiveEvent<Int>() }
    val progressDialogEvent: MutableLiveData<ProgressDialogEvent> by lazy { SingleLiveEvent<ProgressDialogEvent>() }

    private val disposables: MutableList<Disposable> = mutableListOf()

    fun Disposable.lifeCycle(): Disposable {
        disposables.add(this)
        return this@lifeCycle
    }

    @CallSuper
    open fun onProgressDialogDismissed() {
        disposeAll()
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposeAll()
    }

    private fun disposeAll() {
        disposables.asSequence()
                .filterNot { it.isDisposed }
                .forEach { it.dispose() }
        disposables.clear()
    }
}