

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 14:11
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Test {
  const val junit = "junit:junit:4.13.2"
  const val `junit-android` = "androidx.test.ext:junit:1.1.3"
  const val `espresso-core` = "androidx.test.espresso:espresso-core:3.4.0"
}

fun Project.dependTestBase() {
  dependencies {
    "testImplementation"(Test.junit)
    "androidTestImplementation"(Test.`junit-android`)
    "androidTestImplementation"(Test.`espresso-core`)
  }
}

fun Project.dependTestLiveData() {
  dependencies {
    "testImplementation"(Lifecycle.`core-testing`)
  }
}

fun Project.dependTestRoom() {
  dependencies {
    "testImplementation"(Room.`room-testing`)
  }
}

fun Project.dependTestPaging() {
  dependencies {
    "testImplementation"(Paging.`paging-testing`)
  }
}

fun Project.dependTestNavigation() {
  dependencies {
    "testImplementation"(Navigation.`navigation-testing`)
  }
}
