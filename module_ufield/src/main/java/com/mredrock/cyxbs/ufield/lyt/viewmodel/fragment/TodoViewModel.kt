package com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.repository.CheckRepository

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

    //审核是否通过
    private val _isPassSuccess = MutableLiveData<ApiStatus>()
    val isPassSuccess: LiveData<ApiStatus> get() = _isPassSuccess


    init {
        getTodoData()
    }

    /**
     * 获取待审核活动的数据
     */

    @SuppressLint("CheckResult")
     fun getTodoData() {
        CheckRepository
            .receiveTodoData()
            .mapOrInterceptException { Log.e("TodoApiError", it.toString()) }
            .doOnError { Log.e("TodoApiError", it.toString()) }
            .safeSubscribeBy {
                _todoList.postValue(it)
            }
    }
    @SuppressLint("CheckResult")
    fun getTodoUpData(lowerID:Int) {
        CheckRepository
            .receiveTodoUpData(lowerID)
            .mapOrInterceptException { Log.e("getTodoUpData", it.toString()) }
            .doOnError { Log.e("getTodoUpData", it.toString()) }
            .safeSubscribeBy {
                _todoList.value= _todoList.value?.plus(it)
            }
    }


    /**
     * 通过审核
     */
    fun passActivity(id: Int) {
        CheckRepository
            .sendPass(id)
            .doOnError {
                Log.d("passActivity", "测试结果-->>$it ")
            }
            .safeSubscribeBy {
                _isPassSuccess.postValue(it)
            }
    }

    /**
     * 驳回活动
     */
    fun rejectActivity(id: Int, reason: String = "好好好") {
        CheckRepository
            .sendReject(id, reason)
            .doOnError {
                Log.d("rejectActivity", "测试结果-->> ");
            }
            .safeSubscribeBy {
                _isPassSuccess.postValue(it)
                Log.d("48485454", "测试结果-->> ${it.info}");
            }
    }
}