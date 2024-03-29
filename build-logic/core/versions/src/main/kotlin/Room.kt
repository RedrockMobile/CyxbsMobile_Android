import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import com.google.devtools.ksp.gradle.KspExtension

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
  // ksp 按需引入
  apply(plugin = "com.google.devtools.ksp")
  extensions.configure<KspExtension> {
    arg("room.schemaLocation", "${project.projectDir}/schemas") // room 的架构导出目录
    // https://developer.android.com/jetpack/androidx/releases/room#compiler-options
    // 启用 Gradle 增量注释处理器
    arg("room.incremental", "true")
  }
  dependencies {
    "implementation"(Room.`room-runtime`)
    "implementation"(Room.`room-ktx`)
    "ksp"(Room.`room-compiler`)
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