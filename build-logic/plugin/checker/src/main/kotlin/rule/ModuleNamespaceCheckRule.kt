package check.rule

import check.ICheckRule
import org.gradle.api.Project
import java.io.File

/**
 * 项目命名空间检查，主要用于新建项目的时候没有按规范写好模块包名
 *
 * @author 985892345
 * 2022/12/20 17:42
 */
object ModuleNamespaceCheckRule : ICheckRule {
  
  // TODO 这里面是用于兼容特殊模块的，请不要私自添加 ！！！
  private val specialModuleNameSpaceMap = mapOf(
    "lib_crash" to "com.mredrock.lib.crash",
    "lib_base" to "com.mredrock.cyxbs.lib.base",
    "lib_debug" to "com.mredrock.cyxbs.lib.debug",
    "lib_utils" to "com.mredrock.cyxbs.lib.utils",
    "module_app" to "com.mredrock.cyxbs",
    "module_emptyroom" to "com.mredrock.cyxbs.discover.emptyroom",
    "module_grades" to "com.mredrock.cyxbs.discover.grades",
    "module_map" to "com.mredrock.cyxbs.discover.map",
    "module_news" to "com.mredrock.cyxbs.discover.news",
    "module_notification" to "com.redrock.module_notification",
    "module_todo" to "com.cyxbsmobile_single.module_todo",
  )
  
  /**
   * 得到正确的 namespace
   */
  fun getCorrectNamespace(project: Project): String {
    // 分三种情况
    // 一：被登记了的特殊模块
    val specialNamespace = specialModuleNameSpaceMap[project.name]
    if (specialNamespace != null) {
      return specialNamespace
    }
    return if (project.projectDir.parentFile == project.rootDir) {
      // 二：是一级模块
      // 一级模块以 com.mredrock.cyxbs.xxx 命名
      "com.mredrock.cyxbs." + project.name.substringAfter("_")
    } else {
      // 三：是二级模块
      // 二级模块以 com.mredrock.cyxbs.[module|lib|api].xxx 命名
      "com.mredrock.cyxbs." + project.name.replace("_", ".")
    }
  }
  
  override fun onConfigBefore(project: Project) {
    val namespace = getCorrectNamespace(project)
    val file = project.projectDir
      .resolve("src")
      .resolve("main")
      .resolve("java")
      .resolve(namespace.replace(".", File.separator))
    if (!file.exists()) {
      val rule = """
        
        模块包名命名规范：
        1、一级模块
          一级模块以 com.mredrock.cyxbs.xxx 包名命名。如：module_course 为 com.mredrock.cyxbs.course
        2、二级模块
          一级模块以 com.mredrock.cyxbs.[module|lib|api].xxx 包名命名。如：api_course 为 com.mredrock.cyxbs.api.course
          
        你当前 ${project.name} 模块的包名应该改为：$namespace
        
      """.trimIndent()
      throw RuntimeException("${project.name} 模块包名错误！" + rule)
    }
  }
  
  override fun onConfigAfter(project: Project) {
  }
}