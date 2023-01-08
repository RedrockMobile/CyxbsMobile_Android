

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 一个裁剪图片的库
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/16 15:28
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object UCrop {
  // https://github.com/Yalantis/uCrop
  const val ucrop = "com.github.yalantis:ucrop:2.2.8-native"
}

fun Project.dependUCrop() {
  dependencies {
    "implementation"(UCrop.ucrop)
  }
}