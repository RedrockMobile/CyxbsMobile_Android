package com.mredrock.cyxbs.lib.base.operations

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 21:01
 */
abstract class OperationFragment : Fragment {
  constructor() : super()
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
  
}