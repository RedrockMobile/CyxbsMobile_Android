package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:21
 */
class LibProject(project: Project) : BaseLibraryProject(project) {
  override fun initProject() {
  }
  
  /**
   * 不需要依赖自己模块下的子模块时在这里写上，但只提供测试使用，请测试后取消
   */
  override fun isDependChildModule(): Boolean {
//    if (name == "lib_account") {
//      println(name)
//      return false
//    }
    return super.isDependChildModule()
  }
}