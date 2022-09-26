package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.affair.model.AffairRepository
import com.mredrock.cyxbs.affair.room.AffairDataBase
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/11 10:33
 */
class EditAffairViewModel : BaseViewModel() {

  private val _affairEntity = MutableLiveData<AffairEntity>()
  val affairEntity: LiveData<AffairEntity>
    get() = _affairEntity

  fun updateAffair(
    onlyId: Int,
    time: Int,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ) {
    AffairRepository.updateAffair(onlyId, time, title, content, atWhatTime)
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy { "更新成功".toast() }
  }

  fun findAffairEntity(onlyId: Int) {
    val stuNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (stuNum.isNotEmpty()) {
      viewModelScope.launch(Dispatchers.IO) {
        AffairDataBase.INSTANCE.getAffairDao()
          .findAffairByOnlyId(stuNum, onlyId)
          ?.let {
            _affairEntity.postValue(it)
          }
      }
    }
  }
  
//  private fun updateRemind(
//    title: String,
//    description: String,
//    startRow: Int,
//    period: Int,
//    week: Int,
//    remindMinutes: Int
//  ) {
//    CalendarUtils.addCalendarEventRemind(
//      requireActivity(),
//      title,
//      description,
//      TimeUtils.getBegin(startRow, week),
//      TimeUtils.getDuration(period),
//      TimeUtils.getRRule(week),
//      remindMinutes,
//      object : CalendarUtils.OnCalendarRemindListener {
//        override fun onFailed(error_code: CalendarUtils.OnCalendarRemindListener.Status?) {
//          "更新失败".toast()
//        }
//
//        override fun onSuccess() {
//          "更新日历成功".toast()
//        }
//      })
//  }
}