import api.utils.ApiDependUtils
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.apply

/**
 * .
 *
 * @author 985892345
 * @date 2024/2/15 16:37
 */
class LibSinglePlugin : BasePlugin() {

  override fun PluginScope.configure() {
    apply(plugin = "base.library")
    if (project.gradle.startParameter.taskNames.any { it.contains(":lib_single:assembleDebug") }) {
      // 在 AS 使用锤子对 lib_single 模块单独进行构建时，
      // 会因为 AndroidManifest 文件没有设置 single_module_app_name 会构建失败，
      // 所以这里就设置一个默认的名字
      // 单模块的名字在另一个地方设置的
      androidLib {
        defaultConfig {
          manifestPlaceholders["single_module_app_name"] = project.name
        }
      }
    }
    implementationApiParentModule(project)
  }

  /**
   * 单模块调试需要反向依赖 api 模块的实现模块，不然不会加入编译路径，ARouter 会报空指针
   *
   * 但存在 单模块A 依赖了 模块B，模块B 依赖了 api模块C，这种间接依赖 api 模块也要进行处理
   *
   * 所以该方法就是为了进行递归遍历整个依赖树，将所有间接或直接的 api 依赖都同时依赖上该 api 的实现模块
   *
   * 但 api 的实现模块是不能直接得到了，为了解决这个问题，需要各位遵守规范 !!!!!
   * 将所有的 api 依赖及实现模块统一写在 [ApiDepend] 中
   */
  private fun implementationApiParentModule(project: Project) {
    // 通过当前 gradle 运行参数拿到执行单模块调试的 project
    val singleModuleEntryProject = project.gradle.startParameter
      .taskNames
      .singleOrNull()
      ?.let {
        if (it.endsWith(":assembleDebug")) {
          project.project(it.substringBefore(":assembleDebug"))
        } else null
      } ?: return
    // 单模块调试的 project 主动依赖了 lib_single，这里 lib_single 再依赖所有 api 的实现模块
    treeWalkDependencies(
      project,
      singleModuleEntryProject,
      mutableSetOf(),
      mutableSetOf(project, singleModuleEntryProject)
    )
  }

  /**
   * @param mainProject 添加依赖的模块
   * @param observeProject 被观察的模块
   * @param observed 已经被观察的模块
   * @param depended 已经被依赖的模块
   */
  private fun treeWalkDependencies(
    mainProject: Project,
    observeProject: Project,
    observed: MutableSet<Project>,
    depended: MutableSet<Project>
  ) {
    // 已经被观察的模块就取消观察
    if (observed.contains(observeProject)) return
    observed.add(observeProject)
    observeProject.configurations.all {
      if (name != "api" && name != "implementation") return@all
      dependencies.all {
        if (this is ProjectDependency) {
          if (!dependencyProject.name.startsWith("api_")) {
            // 递归观察该 Project 的依赖树
            treeWalkDependencies(mainProject, dependencyProject, observed, depended)
          } else {
            ApiDependUtils.apiWithImplMap[dependencyProject.path]
              ?.getImplPaths()
              ?.mapNotNull { mainProject.findProject(it) }
              ?.filter { it !in depended }
              ?.forEach {
                depended.add(it)
                // 添加依赖
                // 不使用 runtimeOnly 因为 DataBinding 的加载需要编译时显示引用
                mainProject.dependencies.add("implementation", it)
                // 递归观察该 Project 的依赖树
                treeWalkDependencies(mainProject, it, observed, depended)
              }
          }
        }
      }
    }
  }
}