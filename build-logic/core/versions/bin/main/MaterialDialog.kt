

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/16 10:05
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object MaterialDialog {
  // https://github.com/afollestad/material-dialogs
  const val dialog = "com.afollestad.material-dialogs:core:3.3.0"
}

fun Project.dependMaterialDialog() {
  dependencies {
    "implementation"(MaterialDialog.dialog)
  }
}