package com.mredrock.cyxbs.api.course

import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.Serializable

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/3 12:42
 */
interface ILinkService : IProvider {
  
  /**
   * 得到关联人
   */
  fun getLinkStu(): Single<LinkStu>
  
  /**
   * 观察当前登录人的我的关联数据
   * - 支持换账号登录后返回新登录人的数据
   * - 没登录返回的数据全为空串
   * - 第一次观察时会请求新的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 上游不会抛出错误到下游
   *
   * ## 注意
   * - 观察后是一定有值发送下来的，请使用 [LinkStu.isNull] 来判断是否存在关联人
   */
  fun observeSelfLinkStu(): Observable<LinkStu>
  
  data class LinkStu(
    val selfNum: String, // 自己的学号
    val linkNum: String, // 关联人的学号
    val linkMajor: String, // 关联人的专业
    val linkName: String, // 关联人的姓名
    val isShowLink: Boolean, // 是否显示
    val isBoy: Boolean, // 关联人性别
  ) : Serializable {
    
    fun isNull(): Boolean {
      return linkNum.isBlank() || selfNum.isBlank()
    }
    
    fun isNotNull(): Boolean {
      return !isNull()
    }
  }
}