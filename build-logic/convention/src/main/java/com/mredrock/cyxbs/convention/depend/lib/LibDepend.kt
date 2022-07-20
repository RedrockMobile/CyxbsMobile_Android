package com.mredrock.cyxbs.convention.depend.lib

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 11:24
 */
object LibDepend {
  const val account = "lib_account"
  const val common = ":lib_common"
  const val protocol = "lib_protocal"
  const val update = "lib_update"
}

fun Project.dependLibAccount() {
  dependencies {
    "implementation"(project(LibDepend.account))
  }
}

/**
 * 除了 api 模块和 lib_common 模块，其他 Android 模块默认导入
 *
 * api 模块如果导入了，可能出现循环依赖，因为有时候 lib_common 模块也需要依赖 api 模块，
 * 所以 api 模块不应该设置得过于复杂
 */
internal fun Project.dependLibCommon() {
  dependencies {
    "implementation"(project(LibDepend.common))
  }
}

fun Project.dependLibProtocal() {
  dependencies {
    "implementation"(project(LibDepend.protocol))
  }
}

fun Project.dependLibUpdate() {
  dependencies {
    "implementation"(project(LibDepend.update))
  }
}
