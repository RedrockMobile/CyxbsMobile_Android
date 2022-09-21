package com.mredrock.cyxbs.api.course

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/9 16:39
 */
interface ICourseService : IProvider {
  
  /**
   * 显示主页的课表
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   */
  fun replaceHomeFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int
  )
}