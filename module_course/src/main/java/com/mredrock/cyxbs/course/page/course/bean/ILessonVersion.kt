package com.mredrock.cyxbs.course.page.course.bean

import androidx.annotation.WorkerThread
import com.mredrock.cyxbs.course.BuildConfig
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.course.room.LessonVerEntity
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * 比对课表版本号所必须的变量
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/2 22:27
 */
sealed interface ILessonVersion {
  val num: String
  val version: String
  
  /**
   * 比对版本号
   *
   * 根据远古代码留下的注释，学长们担心远端服务器出现数据回退问题，故而提供版本号来防止直接使用远端数据
   *
   * @param defaultWhenSame 当版本号大小相等的时候，决定是否更新，默认为 true
   */
  @WorkerThread
  fun judgeVersion(defaultWhenSame: Boolean): Boolean {
    // 版本号保存于数据库中
    val oldVersion = LessonDataBase.INSTANCE.getLessonVerDao()
      .getVersion(num)?.version ?: "0.0.0"
    val newVersionList = version.split(".")
    val oldVersionList = oldVersion.split(".")
    if (newVersionList.size != oldVersionList.size) {
      // 不应该出现这种情况，因为版本号规定形式为：0.0.0
      // 如果出现，可以认为是远端出现问题，所以就不对本地数据进行更新
      if (BuildConfig.DEBUG) {
        toast("课表接口 version 字段错误：$version")
      }
      return false
    }
    for (i in oldVersionList.indices) {
      if (newVersionList[i] > oldVersionList[i]) {
        LessonDataBase.INSTANCE.getLessonVerDao()
          .insertVersion(LessonVerEntity(num, version))
        return true
      } else if (newVersionList[i] < oldVersionList[i]) {
        return false
      }
    }
    // 都相等的时候，由调用者决定是否更新
    return defaultWhenSame
  }
}