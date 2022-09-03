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
   * 观察当前登录人的关联情况
   */
  fun observeSelfLinkStu(): Observable<LinkStu>
  
  data class LinkStu(
    val selfNum: String, // 自己的学号
    val linkNum: String, // 关联人的学号
    val linkMajor: String, // 关联人的专业
    val linkName: String, // 关联人的姓名
    val isShowLink: Boolean // 是否显示
  ) : Serializable
}