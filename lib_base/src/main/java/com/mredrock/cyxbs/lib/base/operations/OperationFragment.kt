package com.mredrock.cyxbs.lib.base.operations

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.lib.base.ui.BaseUi

/**
 *
 * 业务层的 Fragment
 *
 * 主要用于书写与业务相耦合的代码，比如需要使用到 api 模块时
 *
 *
 *
 *
 *
 *
 * # 更多封装请往父类和接口查看
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 21:01
 */
abstract class OperationFragment : Fragment, BaseUi {
  constructor() : super()
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
  
}