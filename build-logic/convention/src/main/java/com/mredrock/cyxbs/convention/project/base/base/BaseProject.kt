package com.mredrock.cyxbs.convention.project.base.base

import org.gradle.api.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:19
 */
@Suppress("UsePropertyAccessSyntax")
abstract class BaseProject(project: Project) : Project by project {
  
  fun apply() {
    initProjectInternal()
  }
  
  protected open fun initProjectInternal() {
    initProject()
  }
  
  protected abstract fun initProject()
}