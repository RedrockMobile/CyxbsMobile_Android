package com.mredrock.cyxbs.course.page.find.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.course.page.find.network.FindApiServices
import com.mredrock.cyxbs.course.page.find.room.FindStuEntity
import com.mredrock.cyxbs.course.page.find.room.HistoryDataBase
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.asFlow
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
class FindStuViewModel : BaseViewModel() {
  
  private val _studentSearchData = MutableSharedFlow<List<FindStuBean>>()
  val studentSearchData: SharedFlow<List<FindStuBean>>
    get() = _studentSearchData
  
  private val _studentHistory = MutableLiveData<List<FindStuEntity>>()
  val studentHistory: LiveData<List<FindStuEntity>>
    get() = _studentHistory
  
  private val _linkStudent = MutableLiveData<LinkStuEntity?>(null)
  val linkStudent: LiveData<LinkStuEntity?>
    get() = _linkStudent
  
  fun searchStudents(stu: String) {
    FindApiServices::class.api
      .getStudents(stu)
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
        _studentSearchData.emit(it)
      }
  }
  
  fun deleteHistory(num: String) {
    viewModelScope.launch(Dispatchers.IO) {
      HistoryDataBase.INSTANCE.getStuDao()
        .deleteStuFromNum(num)
    }
  }
  
  fun changeLinkStudent(stuNum: String) {
    LinkRepository.changeLinkStudent(stuNum)
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy()
  }
  
  init {
    // 数据库返回的 Observable，属于响应式查询，任何更改都会重新发出更新的通知
    HistoryDataBase.INSTANCE.getStuDao()
      .observeAllStu()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .distinctUntilChanged()
      .safeSubscribeBy {
        _studentHistory.value = it
      }
    
    LinkRepository.observeLinkStudent()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        if (it.isNotNull()) {
          _linkStudent.value = it
        } else {
          _linkStudent.value = null
        }
      }
  }
}