package com.mredrock.cyxbs.course.page.find.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.course.page.find.network.FindApiServices
import com.mredrock.cyxbs.course.page.find.room.HistoryDataBase
import com.mredrock.cyxbs.course.page.find.room.FindTeaEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.asFlow
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 17:54
 */
class FindTeaViewModel : BaseViewModel() {

  private val mTeaDao by lazyUnlock {
    HistoryDataBase.INSTANCE.getTeaDao()
  }

  private val _teacherSearchData = MutableSharedFlow<List<FindTeaBean>>()
  val teacherSearchData = _teacherSearchData.asSharedFlow()

  private val _teacherHistory = MutableLiveData<List<FindTeaEntity>>()
  val teacherHistory: LiveData<List<FindTeaEntity>>
    get() = _teacherHistory

  fun searchTeachers(tea: String) {
    FindApiServices::class.api
      .getTeachers(tea)
      .subscribeOn(Schedulers.io())
      .asFlow()
      .onStart {
//        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
      }.onCompletion {
//        progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
      }.mapOrThrowApiException()
      .catch {
        toast("网络似乎开小差了")
      }.collectLaunch {
        _teacherSearchData.emit(it)
      }
  }

  fun deleteHistory(num: String) {
    viewModelScope.launch(Dispatchers.IO) {
      mTeaDao.deleteTeaFromNum(num)
    }
  }

  init {
    // 只需要刷新一次即可，后面的数据的更改都会重新再次发送到 UI 界面
    // 数据库返回的 Flow，属于响应式查询，任何更改都会重新发出更新的通知
    mTeaDao.observeAllTea()
      .distinctUntilChanged()
      .collectLaunch {
        _teacherHistory.value = it
      }
  }
}