

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:14
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Navigation {
  // navigation 因为有些坑，所以暂不使用，大部分情况可以使用 vp2 代替
  // https://developer.android.com/guide/navigation/navigation-getting-started#Set-up
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#navigation
  const val navigation_version = "2.6.0"
  const val `navigation-runtime-ktx` = "androidx.navigation:navigation-runtime-ktx:$navigation_version"
  const val `navigation-fragment-ktx` = "androidx.navigation:navigation-fragment-ktx:$navigation_version"
  const val `navigation-ui-ktx` = "androidx.navigation:navigation-ui-ktx:$navigation_version"
  
  // Testing Navigation
  const val `navigation-testing` = "androidx.navigation:navigation-testing:$navigation_version"
}

/**
 * navigation 目前返回栈还不算是多返回栈
 *
 * 1、如果是首页多个页面的情况，更建议用 vp2
 * 2、如果 Fragment 进出属于同一条线上时，这时才符合 navigation 的正确用法
 * 3、所以我个人建议是：能用 vp2 的都用 vp2，navigation 应该用在 vp2 的某一个页面中
 */
@Deprecated("navigation 因为有些”坑“，所以暂不使用，大部分情况可以使用 vp2 代替")
fun Project.dependNavigation() {
  dependencies {
    "implementation"(Navigation.`navigation-runtime-ktx`)
    "implementation"(Navigation.`navigation-fragment-ktx`)
    "implementation"(Navigation.`navigation-ui-ktx`)
  }
}