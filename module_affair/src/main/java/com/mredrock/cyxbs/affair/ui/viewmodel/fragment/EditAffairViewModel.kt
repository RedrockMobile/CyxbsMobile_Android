package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.affair.net.AffairRepository
import com.mredrock.cyxbs.affair.service.AffairDataBase
import com.mredrock.cyxbs.affair.service.AffairEntity
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
    id: Int,
    time: Int = 1,
    title: String,
    content: String,
    atWhatTime: List<AffairEntity.AtWhatTime>,
  ) {
    "hahaha".toast()
    AffairRepository.updateAffair(id, time, title, content, atWhatTime)
      .observeOn(AndroidSchedulers.mainThread()).doOnError {
      it.printStackTrace()
    }.safeSubscribeBy { "更新成功".toast() }
  }


  fun findAffairEntity(affairId: Int) {
    val stuNum = ServiceManager(IAccountService::class).getUserService().getStuNum()
    if (stuNum.isNotEmpty()) {
      viewModelScope.launch(Dispatchers.IO) {
        AffairDataBase.INSTANCE.getAffairDao()
          .getAffairById(stuNum, affairId)
          ?.let {
            _affairEntity.postValue(it)
          }
      }
    }
  }

  private fun getAffair() {
    AffairRepository.getAffair().observeOn(AndroidSchedulers.mainThread())
      .doOnError { it.printStackTrace() }.safeSubscribeBy {
      "获取成功".toast()
    }
  }

  init {
    getAffair()
  }

}