package com.mredrock.cyxbs.affair.ui.viewmodel.activity

import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/3 12:16
 */
class AffairViewModel : BaseViewModel() {

  private val _clickAffect = MutableSharedFlow<Unit>()
  val clickAffect: SharedFlow<Unit>
    get() = _clickAffect

  val clickBack : SharedFlow<Unit> get() = _clickBack
  private val _clickBack = MutableSharedFlow<Unit>()

  fun clickNextBtn() {
    viewModelScope.launch {
      _clickAffect.emit(Unit)
    }
  }

  /**
   * 目前仅支持没课越
   */
  fun clickLastBtn(){
    viewModelScope.launch {
      _clickBack.emit(Unit)
    }
  }
}