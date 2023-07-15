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
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
interface ILessonVersion {
  val num: String
  val version: String
  
  /**
   * 比对版本号
   *
   * 根据远古代码留下的注释，学长们担心远端服务器出现数据回退问题，故而提供版本号来防止直接使用远端数据
   *
   * 但在 22 年 10 月后，后端课表接口已经采用 jwzx 的官方接口，所以该问题已经不会再出现，但我仍保留了这个逻辑
   *
   * @param defaultWhenSame 当版本号大小相等的时候，决定是否更新
   */
  @WorkerThread
  fun judgeVersion(defaultWhenSame: Boolean): Boolean {
    // 版本号保存于数据库中
    val oldVersion = LessonDataBase.INSTANCE.getLessonVerDao()
      .findVersion(num)?.version ?: "0.0.0"
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