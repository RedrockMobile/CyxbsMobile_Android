package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:22
 */
class LibCommonProject(project: Project) : BaseLibraryProject(project) {
  override fun initProject() {
    // 这里面只依赖带有 internal 修饰的
    dependAndroidView()
    dependAndroidKtx()
    dependLifecycleKtx()
    dependNetworkInternal()
  }
}