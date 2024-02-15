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
  }
}