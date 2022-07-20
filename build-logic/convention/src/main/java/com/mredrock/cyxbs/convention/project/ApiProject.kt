package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:27
 */
class ApiProject(project: Project) : BaseLibraryProject(project) {
  override fun initProject() {
    // api 模块不主动依赖 lib_common，应尽量做到只有接口和简单逻辑
  }
}