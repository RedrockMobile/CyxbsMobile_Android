

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:41
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Glide {
  // https://github.com/bumptech/glide
  const val glide_version = "4.15.1"
  
  const val glide = "com.github.bumptech.glide:glide:$glide_version"

  // 该注解用于 Glide 生成自定义 API，类似于 kt 的扩展函数，不如直接使用 kt 的扩展函数
//  const val `glide-compiler` = "com.github.bumptech.glide:compiler:$glide_version"
}

fun Project.dependGlide() {
  dependencies {
    "implementation"(Glide.glide)
  }
}