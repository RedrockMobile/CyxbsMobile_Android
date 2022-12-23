package check

import check.rule.ModuleNamespaceCheckRule
import org.gradle.api.Project

/**
 * 用于检查 Android 项目是否规范
 *
 * @author 985892345
 * 2022/12/20 17:37
 */
object AndroidProjectChecker {
  
  // TODO 在这里进行注册
  private val checkRules = arrayOf(
    ModuleNamespaceCheckRule
  )
  
  /**
   * 配置插件前触发
   */
  fun configBefore(project: Project) {
    try {
      checkRules.forEach {
        it.onConfigBefore(project)
      }
    } catch (e: RuntimeException) {
      println("项目检查工具发现问题：${e.message}")
      throw RuntimeException(e.message +  hint)
    }
  }
  
  /**
   * 配置插件后触发
   */
  fun configAfter(project: Project) {
    try {
      checkRules.forEach {
        it.onConfigAfter(project)
      }
    } catch (e: RuntimeException) {
      println("项目检查工具发现问题：${e.message}")
      throw RuntimeException(e.message + hint)
    }
  }
  
  // gradle 默然字符集会导致中文乱码，所以这里单独写了个英文提示
  private val hint = """
    
    =====================================================================================================
    =  NOTE: If you have garbled Chinese characters, such as: 一二三                                     =
    =  place click "Help - Edit Custom VM Options" and then add "-Dfile.encoding=UTF-8" and restart AS  =
    =====================================================================================================
    
  """.trimIndent()
}