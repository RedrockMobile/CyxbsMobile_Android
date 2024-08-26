package com.mredrock.cyxbs.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.bean.ActivityBean
import com.mredrock.cyxbs.ufield.bean.Todo
import com.mredrock.cyxbs.ufield.bean.TodoListPushWrapper
import com.mredrock.cyxbs.ufield.network.ActivityDetailApiService
import com.mredrock.cyxbs.ufield.network.UFieldApiService
import com.mredrock.cyxbs.ufield.repository.UFieldRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/24 10:49
 */
class DetailViewModel(id:Int) :BaseViewModel(){
private val _detailData= MutableLiveData<ActivityBean>()
    val detailData: LiveData<ActivityBean>
        get() = _detailData
private val _wantToSee=MutableLiveData<Boolean>()

    val wantToSee:LiveData<Boolean>
        get() = _wantToSee
    init {
        getActivityData(id)
    }
    private fun getActivityData(id:Int){
        ActivityDetailApiService::class.api
            .getActivityData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("请求失败")
            }
            .safeSubscribeBy {
                _detailData.postValue(it)
            }

    }
    fun wantToSee(id:Int){
        ActivityDetailApiService::class.api
            .wantToSee(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                _wantToSee.postValue(false)
            }
            .safeSubscribeBy {
               _wantToSee.postValue(true)
            }
    }

    fun addTodo(todo: Todo) {
        val pushWrapper = TodoListPushWrapper(
            listOf(todo),
            getLastSyncTime()
        )
        UFieldRepository.pushTodo(pushWrapper)
            .safeSubscribeBy {
                it.data.syncTime.apply {
                    setLastSyncTime(this)
                }
            }
    }

    /**
     * 得到和设置本地最后同步的时间戳
     */
    private fun getLastSyncTime(): Long =
        appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)

    private fun setLastSyncTime(syncTime: Long) {
        appContext.getSp("todo").edit().apply {
            putLong("TODO_LAST_SYNC_TIME", syncTime)
            commit()
        }
    }

}