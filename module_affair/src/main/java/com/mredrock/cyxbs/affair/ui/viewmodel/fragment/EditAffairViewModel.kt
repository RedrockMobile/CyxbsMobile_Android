package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.affair.model.AffairRepository
import com.mredrock.cyxbs.affair.room.AffairDataBase
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

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
      AffairDataBase.INSTANCE.getAffairDao()
        .findAffairByOnlyId(stuNum, onlyId)
        .subscribeOn(Schedulers.io())
        .safeSubscribeBy {
          _affairEntity.postValue(it)
        }
    }
  }
}