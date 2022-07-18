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
    // lib_common 默认情况下是导入所有必要的依赖
    // 除了 Bugly、Sophix 等一些只需要 module_app 模块才需要
    dependAndroidView()
    dependAndroidKtx()
    dependCoroutines()
    dependCoroutinesRx3()
    dependEventBus()
    dependGlide()
    dependLifecycleKtx()
    dependLottie()
    dependLPhotoPicker()
    dependMaterialDialog()
    dependNetwork()
    dependNetworkInternal()
    dependPaging()
    dependPhotoView()
    dependRoom()
    dependRoomRxjava()
    dependRoomPaging()
    dependRxPermissions()
  }
}