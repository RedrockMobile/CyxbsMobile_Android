package com.mredrock.cyxbs.api.course

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * 打开查找他人课表界面的服务类
 *
 * @author 985892345
 * @date 2022/9/22 15:50
 */
interface IFindLessonService : IProvider {
  
  /**
   * 直接打开查找他人课表的 Activity
   */
  fun startActivity(context: Context)
  
  /**
   * 打开学号为 [stuNum] 的学生课表
   */
  fun startActivityByStuNum(context: Context, stuNum: String)
  
  /**
   * 定向查找名字包含 [stuName] 的学生
   */
  fun startActivityByStuName(context: Context, stuName: String)
  
  /**
   * 打开工号为 [teaNum] 的老师课表
   */
  fun startActivityByTeaNum(context: Context, teaNum: String)
  
  /**
   * 定向查看名字包含 [teaName] 的老师
   */
  fun startActivityByTeaName(context: Context, teaName: String)
}