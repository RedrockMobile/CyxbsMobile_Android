package com.mredrock.cyxbs.convention.publish

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.mredrock.cyxbs.convention.publish.Cache

plugins {
  `maven-publish`
}

if (plugins.hasPlugin("com.android.application")) {
  extensions.configure<BaseAppModuleExtension> {
    publishing {
      singleVariant("debug")
    }
  }
} else if (plugins.hasPlugin("com.android.library")) {
  extensions.configure<LibraryExtension> {
    publishing {
      singleVariant("debug")
    }
  }
} else {
  throw RuntimeException("只允许给 application 和 library 进行缓存，如有其他模块，请额外实现逻辑！")
}

// 增加 cache 闭包
val cache = extensions.create("cache", Cache::class, project)

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("moduleCache") {
        from(components["debug"])
      }
      
      // https://docs.gradle.org/current/userguide/publishing_maven.html#header
      repositories {
        maven {
          url = cache.localMavenUri
          group = cache.localMavenGroup
          version = cache.localMavenVersion
          name = cache.localMavenName
        }
      }
    }
  }
}

cache.isAllowSelfUseCache {
  // 允许自身使用缓存的时候
  configurations.all {
    resolutionStrategy.dependencySubstitution.all {
      val requested = requested
      if (requested is ProjectComponentSelector) {
        val projectPath = requested.projectPath
        val otherProject = project(projectPath)
        // 判断当前被依赖的模块是否允许用缓存替换
        if (cache.isAllowOtherUseCache(otherProject)) {
          val file = (otherProject.extensions["cache"] as Cache).getLocalMavenFile()
          if (file.exists()) {
            // 存在就直接替换依赖
            println("当前模块：${project.name}，替换依赖：$projectPath")
            useTarget("${cache.localMavenGroup}:${otherProject.name}:${otherProject.version}")
          }
        }
      }
    }
  }
}


tasks.register("cacheToLocalMaven") {
  group = "publishing"
  /*
  * module_app 因为需要依赖所有模块，但缓存是需要进行完整打包的，
  * 存在某 module 模块是单模块调试状态，这种情况下 module_app 的构建会失败
  *
  * 意思就是：缓存时不允许两个使用 application 插件的模块之间存在依赖关系
  *
  * 但是否存在互相依赖且都同时使用了 application 插件有点难判断，所以 module_app 就默认不缓存了
  * */
  if (project.name != "module_app"
    && !gradle.startParameter.taskNames.any {
      it == "${project.path}:assembleDebug" // 自身模块打包时不允许缓存，因为启动模块在单模块调试时经常被修改
    }
    && cache.isNeedCreateNewCache()
  ) {
    dependsOn("publishModuleCachePublicationTo${cache.localMavenName.capitalize()}Repository")
  }
}

if (name.startsWith("module_") || name.startsWith("lib_")) {
  /*
  * 这里有个很奇怪的问题，如果给 api 模块加上，api 模块会报错：
  * Cannot access built-in declaration 'kotlin.String'. Ensure that you have a dependency on the Kotlin standard library
  * 只要给 api 模块调用了 tasks.whenTaskAdded 就会报，
  * 反正 api 模块不会调用打包，所以就判断了一下
  * */
  tasks.whenTaskAdded {
    if (name == "assembleDebug") {
      dependsOn(rootProject.tasks.named("cacheToLocalMaven"))
    }
  }
}

