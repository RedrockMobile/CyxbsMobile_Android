

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
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

  // Glide注解处理器
  const val `glide-compiler` = "com.github.bumptech.glide:compiler:$glide_version"
}

fun Project.dependGlide(isNeedProcessAnnotation:Boolean=false) {
  //kapt 按需引入
  if (isNeedProcessAnnotation){
    apply(plugin = "org.jetbrains.kotlin.kapt")
    dependencies{
      "kapt"(Glide.`glide-compiler`)
    }
  }
  dependencies {
    "implementation"(Glide.glide)
  }
}