//import com.mredrock.cyxbs.convention.project.ModuleDebugProject
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.kotlin.dsl.apply
//
///**
// * ...
// * @author 985892345 (Guo Xiangrui)
// * @email guo985892345@foxmail.com
// * @date 2022/8/12 12:59
// */
//class ModuleDebugPlugin : Plugin<Project> {
//  override fun apply(target: Project) {
//    target.run {
//      if (isAllowDebugModule()) {
//        doDebugModule()
//      } else {
//        cancelDebugModule()
//      }
//    }
//  }
//
//  // 是否允许执行单模块调试
//  private fun Project.isAllowDebugModule(): Boolean {
//    return !project.gradle.startParameter.taskNames.any {
//      // 注意：这里面的是取反，即满足下面条件的不执行单模块调试
//      it.contains("assembleRelease")
//        || it.contains("assembleDebug") && !it.contains(project.name)
//        || it == "publishModuleCachePublicationToMavenRepository" // 本地缓存任务
//        || it == "cacheToLocalMaven"
//        || it == "channelRelease"
//        || it == "channelDebug"
//    }
//  }
//
//  // 允许执行单模块调试
//  private fun Project.doDebugModule() {
//    ModuleDebugProject(project).apply()
//    apply(plugin = "com.mredrock.cyxbs.convention.publish.publications")
//  }
//
//  // 不允许执行单模块调试
//  private fun Project.cancelDebugModule() {
//    println("${project.name} 的单模块调试被取消！")
//    apply(plugin = "module-manager")
//  }
//}