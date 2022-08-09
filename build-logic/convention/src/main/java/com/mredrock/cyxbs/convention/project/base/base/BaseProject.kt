package com.mredrock.cyxbs.convention.project.base.base

import org.gradle.api.*

/**
 * 最顶层的 Project
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:19
 */
@Suppress("UsePropertyAccessSyntax")
abstract class BaseProject(project: Project) : Project by project {
  
  fun apply() {
    initProjectInternal()
  }
  
  /**
   * 这里多一个是为了达到子类强制调用 super 的效果
   */
  protected open fun initProjectInternal() {
    initProject()
  }
  
  protected abstract fun initProject()
}