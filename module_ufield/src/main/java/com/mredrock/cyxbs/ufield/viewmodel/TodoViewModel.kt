package com.mredrock.cyxbs.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.bean.TodoBean
import com.mredrock.cyxbs.ufield.repository.CheckRepository

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:18
 *  description :
 *  version ： 1.0
 */

class TodoViewModel : BaseViewModel() {

    private val _todoList = MutableLiveData<List<TodoBean>>()
    val todoList: LiveData<List<TodoBean>>
        get() = _todoList
    private val _isPassSuccess = MutableLiveData<ApiStatus>()
    val isPassSuccess: LiveData<ApiStatus> get() = _isPassSuccess


    init {
        getTodoData()
    }


    fun getTodoData() {
        CheckRepository
            .receiveTodoData()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _todoList.postValue(it) }
    }

    fun getTodoUpData(lowerID: Int) {
        CheckRepository
            .receiveTodoUpData(lowerID)
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _todoList.value = _todoList.value?.plus(it) }
    }


    fun passActivity(id: Int) {
        CheckRepository
            .sendPass(id)
            .doOnError {}
            .safeSubscribeBy { _isPassSuccess.postValue(it) }
    }

    /**
     * 驳回活动
     */
    fun rejectActivity(id: Int, reason: String) {
        CheckRepository
            .sendReject(id, reason)
            .doOnError {}
            .safeSubscribeBy { _isPassSuccess.postValue(it) }
    }
}