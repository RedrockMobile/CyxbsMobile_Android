@file:Suppress("UnstableApiUsage")

package com.mredrock.cyxbs.convention.project.base.base

import com.android.build.api.dsl.*
import com.mredrock.cyxbs.convention.depend.dependARouter
import com.mredrock.cyxbs.convention.depend.dependTestBase
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import com.mredrock.cyxbs.convention.config.Config
import com.mredrock.cyxbs.convention.depend.dependAndroidBase
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:34
 */
abstract class BaseAndroidProject(project: Project) : BaseProject(project) {
  
  override fun initProjectInternal() {
    dependencies {
      if (isDependChildModule()) {
        // 自动依赖自己目录下的子模块
        dependChildModule()
      }
    }
    // 本来可以不依赖 Test，但每次那个 test 文件夹都报错，有时候又忘了删，强迫症
    dependTestBase()
    // 所有 Android 工程模块都需要
    dependAndroidBase()
    // 所有 Android 工程模块都需要依赖 ARouter
    dependARouter()
    super.initProjectInternal()
  
    // kapt 的设置
    extensions.configure<KaptExtension> {
      arguments {
        arg("AROUTER_MODULE_NAME", project.name)
        arg("room.schemaLocation", "${project.projectDir}/schemas") // room 的架构导出目录
      }
    }
  }
  
  /**
   * 是否自动依赖自己目录下的子模块
   */
  open fun isDependChildModule(): Boolean = true
  
  /**
   * 统一配置 android 闭包的公共部分
   *
   * 除了部分内容需要单独写，因为他没有写在顶层接口
   */
  protected fun <A : BuildFeatures, B : BuildType, C : DefaultConfig, D : ProductFlavor>
    CommonExtension<A, B, C, D>.uniformConfigAndroid() {
    
    compileSdk = Config.compileSdk
    defaultConfig {
      minSdk = Config.minSdk
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      
      // 添加以下两句代码，这是 photolibrary 需要设置的东西
      renderscriptTargetApi = Config.targetSdk  //版本号请与compileSdkVersion保持一致
      renderscriptSupportModeEnabled = true
    }
    buildTypes {
      release {
        isMinifyEnabled = true
//        isShrinkResources = true
        proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          rootDir.resolve("build-logic")
            .resolve("convention")
            .resolve("proguard-rules.pro")
        )
  
        ndk {
          abiFilters += Config.releaseAbiFilters
        }
      }
      debug {
        isMinifyEnabled = true
//        isShrinkResources = false
        proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          rootDir.resolve("build-logic")
            .resolve("convention")
            .resolve("proguard-rules.pro")
        )
        
        ndk {
          abiFilters += Config.debugAbiFilters
        }
      }
    }
    
    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    lint {
      // 编译遇到错误不退出
      abortOnError = false
      // 错误输出文件
      baseline = project.file("lint-baseline.xml")
      // 未知
      // todo
      disable += listOf("TrustAllX509TrustManager")
    }
    
    (this as ExtensionAware).extensions.configure<KotlinJvmOptions> {
      jvmTarget = "1.8"
    }
  }
  
  /**
   * 自动依赖自己目录下的子模块
   */
  private fun DependencyHandlerScope.dependChildModule() {
    
    // 根 gradle 中包含的所有子模块
    val includeProjects = rootProject.subprojects.map { it.name }
    
    projectDir.listFiles()!!.filter {
      // 1.是文件夹
      // 2.以 lib_ 或者 api_ 开头
      // 3.根 gradle 导入了的模块
      it.isDirectory
        && "(lib_.+)|(api_.+)".toRegex().matches(it.name)
        && includeProjects.contains(it.name)
    }.forEach {
      "implementation"(project(":${name}:${it.name}"))
    }
  }
}