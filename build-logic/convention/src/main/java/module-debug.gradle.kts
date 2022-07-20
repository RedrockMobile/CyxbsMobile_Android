import com.mredrock.cyxbs.convention.project.ModuleDebugProject

// 是否允许执行单模块调试
fun isAllowDebugModule(): Boolean {
  return !gradle.startParameter.taskNames.any {
    // 注意：这里面的是取反，即满足下面条件的不执行单模块调试
    it.contains("assembleRelease")
      || it.contains("assembleDebug") && !it.contains(project.name)
      || it == "publishModuleCachePublicationToMavenRepository" // 本地缓存任务
      || it == "cacheToLocalMaven"
  }
}

// 允许执行单模块调试
fun doDebugModule() {
  ModuleDebugProject(project).apply()
  plugins {
    id("com.mredrock.cyxbs.convention.publish.publications")
  }
}

// 不允许执行单模块调试
fun cancelDebugModule() {
  println("${project.name} 的单模块调试被取消！")
  apply(plugin = "module-manager")
}

if (isAllowDebugModule()) {
  doDebugModule()
} else {
  cancelDebugModule()
}
