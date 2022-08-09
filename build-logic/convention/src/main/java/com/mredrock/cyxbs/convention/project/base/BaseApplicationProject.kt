@file:Suppress("UnstableApiUsage")

package com.mredrock.cyxbs.convention.project.base

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.kotlin.dsl.apply
import com.mredrock.cyxbs.convention.project.base.base.BaseAndroidProject
import com.mredrock.cyxbs.convention.config.Config
import com.mredrock.cyxbs.convention.depend.debugDependLeakCanary
import com.mredrock.cyxbs.convention.depend.debugDependPandora
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
    debugDependLeakCanary() // 依赖 LeakCancry，检查内存泄漏
    debugDependPandora() // 依赖 Pandora，一个很强的手机开发辅助工具 https://www.wanandroid.com/blog/show/2526
    
    // CodeLocator 问题比较多，会疯狂抛被抓的异常出来，影响其他异常，所以暂时先注释
//    debugDependCodeLocator() // 字节在用的极其强大的调试工具：https://github.com/bytedance/CodeLocator
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
    }
  }
}