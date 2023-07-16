import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:05
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Room {
  // https://developer.android.com/jetpack/androidx/releases/room?hl=en
  const val room_version = "2.5.2"
  
  const val `room-runtime` = "androidx.room:room-runtime:$room_version"
  const val `room-compiler` = "androidx.room:room-compiler:$room_version"
  
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#room
  const val `room-ktx` = "androidx.room:room-ktx:$room_version"
  const val `room-rxjava3` = "androidx.room:room-rxjava3:$room_version"
  const val `room-paging` = "androidx.room:room-paging:$room_version"
  
  // optional - Test helpers
  const val `room-testing` = "androidx.room:room-testing:$room_version"
}

fun Project.dependRoom() {
  // kapt 按需引入
  apply(plugin = "org.jetbrains.kotlin.kapt")
  extensions.configure<KaptExtension> {
    arguments {
      arg("room.schemaLocation", "${project.projectDir}/schemas") // room 的架构导出目录
      arg("room.incremental", true)
    }
  }
  dependencies {
    "implementation"(Room.`room-runtime`)
    "implementation"(Room.`room-ktx`)
    "kapt"(Room.`room-compiler`)
  }
}

fun Project.dependRoomRxjava() {
  dependencies {
    "implementation"(Room.`room-rxjava3`)
  }
}

fun Project.dependRoomPaging() {
  dependencies {
    "implementation"(Room.`room-paging`)
  }
}