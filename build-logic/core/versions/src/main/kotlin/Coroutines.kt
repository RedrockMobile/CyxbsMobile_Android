

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:34
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Coroutines {
  // https://developer.android.com/kotlin/coroutines
  const val coroutines_version = "1.7.3"
  
  const val `coroutines-android` = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
  
  // 包含 Flow 与 Rxjava3 的互转
  const val `coroutines-rx3` = "org.jetbrains.kotlinx:kotlinx-coroutines-rx3:$coroutines_version"
}

fun Project.dependCoroutines() {
  dependencies {
    "implementation"(Coroutines.`coroutines-android`)
  }
}

/**
 * 包含 Flow 与 Rxjava3 的互转
 */
fun Project.dependCoroutinesRx3() {
  dependencies {
    "implementation"(Coroutines.`coroutines-rx3`)
  }
}