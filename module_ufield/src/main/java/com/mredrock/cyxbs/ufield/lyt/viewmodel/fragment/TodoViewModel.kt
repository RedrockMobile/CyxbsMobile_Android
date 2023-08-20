package com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.base.utils.safeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.repository.TodoRepository

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:18
 *  description :
 *  version ： 1.0
 */

class TodoViewModel : BaseViewModel() {
    /**
     *未审核活动的livedata
     */
    private val _todoList = MutableLiveData<List<TodoBean>>()
    val todoList: LiveData<List<TodoBean>>
        get() = _todoList

    /**
     * 获取待审核活动的数据
     */

    @SuppressLint("CheckResult")
    fun getTodoData() {
        TodoRepository
            .receiveTodoData()
            .mapOrInterceptException { Log.e("TodoApiError", it.toString()) }
            .doOnError { Log.e("TodoApiError", it.toString()) }
            .safeSubscribeBy {
                _todoList.postValue(it)
            }
    }
}