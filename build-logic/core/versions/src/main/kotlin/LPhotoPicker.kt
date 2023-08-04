

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 一个图片选择框架
 *
 * （不知道为什么以前学长会选择这个）
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:25
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object LPhotoPicker  {
  // https://github.com/limuyang2/LPhotoPicker
  const val lPhotoPicker = "com.github.limuyang2:LPhotoPicker:2.7"
}

fun Project.dependLPhotoPicker() {
  dependencies {
    "implementation"(LPhotoPicker.lPhotoPicker)
  }
}