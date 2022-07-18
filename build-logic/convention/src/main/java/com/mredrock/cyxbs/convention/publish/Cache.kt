package com.mredrock.cyxbs.convention.publish

import org.gradle.api.Project
import java.io.File
import java.net.URI

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/5 16:45
 */
abstract class Cache(private val project: Project) {
  
  /**
   * 是否允许自身使用缓存
   *
   * 注意：设置为 true 并不一定就能成功，内部还会进行判断
   */
  var isAllowSelfUseCache = true
  
  /**
   * 是否需要创建自己的缓存
   *
   * 注意：设置为 true 并不一定就能成功，内部还会进行判断
   */
  var isNeedCreateNewCache = true
  
  /**
   * 排除替换成缓存的项目名
   */
  fun exclude(projectName: String) {
    excludeList.add(projectName)
  }
  
  /**
   * 排除替换成缓存的规则
   */
  fun exclude(func: (Project) -> Boolean) {
    excludeFuncList.add(func)
  }
  
  
  /*
  * ================================================================================================
  * 下面是内部使用的变量和函数
  * */
  
  private val excludeList = mutableListOf<String>()
  private val excludeFuncList = mutableListOf<(Project) -> Boolean>()
  
  // 这里的地址要与 settings.gradle.kts 相对应
  internal val localMavenUri: URI = File(project.rootProject.buildDir, "maven").toURI()
  internal val localMavenGroup = "cache"
  internal val localMavenVersion = project.projectDir.size() // 暂时以文件大小作为版本号，考虑过 MD5，但可能耗时
  internal val localMavenName = "Local"
  
  
  /**
   * 是否允许使用缓存，针对于本身模块，是总开关
   *
   * 这里面主要是写执行哪些 gradle 命令时不开启缓存替换功能
   */
  internal fun isAllowSelfUseCache(ifTrue: () -> Unit): Boolean {
    if (!isAllowSelfUseCache) {
      return false
    }
    val tasks = project.gradle.startParameter.taskNames
    val isAllow = tasks.isNotEmpty() && !tasks.any {
      it == "assembleRelease" // gradle 直接打包
        || it == "assembleDebug"
        || it == ":module_app:assembleRelease"
        || it == "publishModuleCachePublicationToMavenRepository" // 本地缓存任务
        || it == "cacheToLocalMaven"
    }
    return isAllow.also { if (it) ifTrue.invoke() }
  }
  
  /**
   * 判断某模块是否可以被缓存替换，针对于被依赖的模块
   */
  internal fun isAllowOtherUseCache(project: Project): Boolean {
    return !excludeList.contains(project.name)
      || !excludeFuncList.any { it.invoke(project) }
  }
  
  /**
   * 是否需要创建新的缓存
   */
  internal fun isNeedCreateNewCache(): Boolean {
    if (!isNeedCreateNewCache) {
      return false
    }
    val file = getLocalMavenFile()
    // 判断当前版本的文件是否存在
    val isExists = file.exists()
    if (!isExists) {
      // 不存在就递归删除旧的文件
      file.parentFile.deleteRecursively()
    }
    return !isExists
  }
  
  /**
   * 得到本地缓存的文件夹，因为有版本的原因，所以可能会为空
   */
  internal fun getLocalMavenFile(): File {
    return File(localMavenUri).resolve("${localMavenGroup}/${project.name}/${localMavenVersion}")
  }
  
  // 获取文件大小
  private fun File.size(): Long {
    var size = 0L
    if (isFile) {
      return length()
    } else {
      val files = listFiles()
      if (files != null) {
        for (childFile in files) {
          size += childFile.size()
        }
      }
    }
    return size
  }
}