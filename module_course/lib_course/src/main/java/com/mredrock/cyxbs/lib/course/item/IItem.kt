package com.mredrock.cyxbs.lib.course.item

import android.view.View
import com.ndhzs.netlayout.attrs.INetBean
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 13:07
 */
interface IItem : INetBean {
  val view: View
  val lp: NetLayoutParams
}