package check

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2022/12/20 17:39
 */
interface ICheckRule {
  /**
   * 配置插件前触发
   */
  @kotlin.jvm.Throws(RuntimeException::class)
  fun onConfigBefore(project: Project)
  
  /**
   * 配置插件后触发
   */
  @kotlin.jvm.Throws(RuntimeException::class)
  fun onConfigAfter(project: Project)
}