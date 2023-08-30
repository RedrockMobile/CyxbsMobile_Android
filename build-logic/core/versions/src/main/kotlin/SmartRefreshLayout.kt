

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/6 20:49
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection",)
object SmartRefreshLayout {
  // https://github.com/scwang90/SmartRefreshLayout
  
  const val version = "2.0.6"
  
  const val kernel = "io.github.scwang90:refresh-layout-kernel:$version"
  
  // 下拉头
  // 我去看了一下，他没有发布那些花里浮哨刷新头的依赖，不知道为什么
  const val `header-classics` = "io.github.scwang90:refresh-header-classics:$version" //经典刷新头
  const val `header-radar` = "io.github.scwang90:refresh-header-radar:$version" //雷达刷新头
  const val `header-falsify` = "io.github.scwang90:refresh-header-falsify:$version" //虚拟刷新头
  const val `header-material` = "io.github.scwang90:refresh-header-material:$version" //谷歌刷新头
  const val `header-two-level` = "io.github.scwang90:refresh-header-two-level:$version"//二级刷新头
  
  // 上拉尾
  const val `footer-ball` = "io.github.scwang90:refresh-footer-ball:$version" //球脉冲加载
  const val `footer-classics` = "io.github.scwang90:refresh-footer-classics:$version" //经典加载
}

fun Project.dependSmartRefreshLayout() {
  dependencies {
    "implementation"(SmartRefreshLayout.kernel)
    /*
    * 头和尾自己单独依赖！！！
    * 比如在你的 build.gradle.kts 中：
    * ```
    * dependencies {
    *     implementation(SmartRefreshLayout.`header-classics`)
    * }
    * ```
    * */
  }
}