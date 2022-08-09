package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/8/9 15:29
 */
class LibConfigProject(project: Project) : BaseLibraryProject(project) {
  override fun initProject() {
    // 这里面只依赖带有 internal 修饰的
  }
}