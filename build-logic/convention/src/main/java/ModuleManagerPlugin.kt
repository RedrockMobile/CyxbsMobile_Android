import com.mredrock.cyxbs.convention.project.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/12 13:07
 */
class ModuleManagerPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.run {
      /**
       * 不同模块分配不同的插件
       */
      val projectName: String = project.name
      when {
        projectName == "module_app" -> AppProject(project).apply()
        projectName == "lib_common" -> LibCommonProject(project).apply()
        projectName == "lib_base" -> LibBaseProject(project).apply()
        projectName == "lib_utils" -> LibUtilsProject(project).apply()
        projectName == "lib_config" -> LibConfigProject(project).apply()
        projectName == "lib_debug" -> LibDebugProject(project).apply()
        projectName == "lib_crash" -> LibCrashProject(project).apply()
        projectName.startsWith("module_") -> ModuleProject(project).apply()
        projectName.startsWith("lib_") -> LibProject(project).apply()
        projectName.startsWith("api_") -> ApiProject(project).apply()
        else -> throw Exception("出现未知类型模块: name = $projectName   dir = $projectDir\n请为该模块声明对应的依赖插件！")
      }
  
      apply(plugin = "com.mredrock.cyxbs.convention.publish.publications")
    }
  }
}