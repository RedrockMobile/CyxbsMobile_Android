package com.mredrock.cyxbs.course.page.find.viewmodel.activity

import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.course.page.find.bean.IFindPerson
import com.mredrock.cyxbs.course.page.find.room.FindStuEntity
import com.mredrock.cyxbs.course.page.find.room.FindTeaEntity
import com.mredrock.cyxbs.course.page.find.room.HistoryDataBase
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/21 23:58
 */
class ShowResultViewModel : BaseViewModel() {
  
  private val _changeLinkResult = MutableSharedFlow<LinkStuEntity?>()
  val changeLinkResult: SharedFlow<LinkStuEntity?>
    get() = _changeLinkResult
  
  private val _deleteLinkResult = MutableSharedFlow<Boolean>()
  val deleteLinkResult: SharedFlow<Boolean>
    get() = _deleteLinkResult

  /**
   * 保存搜索历史
   */
  fun saveHistory(person: IFindPerson) {
    viewModelScope.launch(Dispatchers.IO) {
      val db = HistoryDataBase.INSTANCE
      when (person) {
        is FindStuBean -> db.getStuDao().insertStu(FindStuEntity(person))
        is FindTeaBean -> db.getTeaDao().insertTea(FindTeaEntity(person))
      }
    }
  }
  
  fun changeLinkStudent(stuNum: String) {
    LinkRepository.changeLinkStudent(stuNum)
      .asFlow()
      .catch {
      }.collectLaunch {
        _changeLinkResult.emit(it)
      }
  }
  
  fun deleteLinkStudent() {
    LinkRepository.deleteLinkStudent()
      .doOnError {
        viewModelScope.launch {
          _deleteLinkResult.emit(false)
        }
      }.safeSubscribeBy {
        viewModelScope.launch {
          _deleteLinkResult.emit(true)
        }
      }
  }
}