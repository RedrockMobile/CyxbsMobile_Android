@file:Suppress("UnstableApiUsage")

package com.mredrock.cyxbs.convention.project.base

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.kotlin.dsl.apply
import com.mredrock.cyxbs.convention.project.base.base.BaseAndroidProject
import com.mredrock.cyxbs.convention.config.Config
import com.mredrock.cyxbs.convention.depend.lib.debugDependLibDebug
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 只管 application 的 Project
 *
 * 包含：module_app 模块和单模块调试时的主模块
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:31
 */
abstract class BaseApplicationProject(project: Project) : BaseAndroidProject(project) {
  
  override fun initProjectInternal() {
    initApplication()
    super.initProjectInternal()
    debugDependLibDebug() // 所有 application 模块默认在 debug 时依赖 lib_debug
  }
  
  protected open fun initApplication() {
    apply(plugin = "com.android.application")
    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-kapt")
    extensions.configure<BaseAppModuleExtension> {
      initAndroid(this)
    }
  }
  
  // 配置 android 闭包
  protected open fun initAndroid(extension: BaseAppModuleExtension) {
    extension.run {
      uniformConfigAndroid()
      defaultConfig {
        applicationId = Config.getApplicationId(project)
        versionCode = Config.versionCode
        versionName = Config.versionName
        targetSdk = Config.targetSdk
      }
  
      buildFeatures {
        dataBinding = true
      }
      
      buildTypes {
        release {
          isShrinkResources = true
        }
      }
  
      packagingOptions {
        jniLibs.excludes += Config.jniExclude
        resources.excludes += Config.resourcesExclude
      }
    }
  }
}