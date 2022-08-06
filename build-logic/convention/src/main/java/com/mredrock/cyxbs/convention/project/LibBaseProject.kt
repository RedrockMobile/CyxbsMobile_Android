package com.mredrock.cyxbs.convention.project

import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.dependAndroidKtx
import com.mredrock.cyxbs.convention.depend.dependAndroidView
import com.mredrock.cyxbs.convention.project.base.BaseLibraryProject
import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/1 13:07
 */
class LibBaseProject(project: Project) : BaseLibraryProject(project) {
  override fun initProject() {
    dependAndroidView()
    dependAndroidKtx()
    dependCoroutines()
    dependCoroutinesRx3()
    dependLifecycleKtx()
  }
}