package com.mredrock.cyxbs.course.page.course.ui.home.helper

import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:02
 */
class RefreshLayoutHelper private constructor(
  val refresh: SmartRefreshLayout,
) {
  
  companion object {
    fun attach(refresh: SmartRefreshLayout): RefreshLayoutHelper {
      return RefreshLayoutHelper(refresh)
    }
  }
  
  init {
    refresh.setEnableRefresh(false)
  }
}