

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 16:43
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Rxjava {
  // https://github.com/ReactiveX/RxJava
  const val rxjava3 = "io.reactivex.rxjava3:rxjava:3.1.6"
  // https://github.com/ReactiveX/RxAndroid
  const val `rxjava3-android` = "io.reactivex.rxjava3:rxandroid:3.0.2"
  // https://github.com/ReactiveX/RxKotlin
  const val `rxjava3-kotlin` = "io.reactivex.rxjava3:rxkotlin:3.0.1"
}

fun Project.dependRxjava() {
  dependencies {
    "implementation"(Rxjava.rxjava3)
    "implementation"(Rxjava.`rxjava3-android`)
    "implementation"(Rxjava.`rxjava3-kotlin`)
  }
}