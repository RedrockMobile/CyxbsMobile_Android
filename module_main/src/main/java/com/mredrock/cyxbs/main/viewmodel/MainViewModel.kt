package com.mredrock.cyxbs.main.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/14 20:50
 */
class MainViewModel : BaseViewModel() {
  /**
   * 三个状态
   * - true -> 展开
   * - false -> 折叠
   * - null -> 隐藏
   */
  val courseBottomSheetExpand = MutableLiveData<Boolean?>()
  val courseBottomSheetOffset = MutableLiveData<Float>()
}