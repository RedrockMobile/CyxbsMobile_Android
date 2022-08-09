package com.mredrock.cyxbs.convention.depend.lib

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 11:24
 */
object LibDepend {
  /*
  * 注意事项：
  * 1、别忘了前面要打引号
  * 2、建议按顺序添加
  * 3、一般情况下只有共用的才会添加，比如像 lib_account 这种，只需要添加它的 api 模块就够了，
  *   没必要添加它的 lib 模块，因为没有其他模块会使用
  * */
  
  const val base = ":lib_base"
  const val common = ":lib_common"
  const val config = ":lib_config"
  const val utils = ":lib_utils"
}

fun Project.dependLibBase() {
  dependencies {
    "implementation"(project(LibDepend.base))
  }
}

/**
 * 除了 api 模块和 lib_common 模块，其他 Android 模块默认导入
 *
 * api 模块如果导入了，可能出现循环依赖，因为有时候 lib_common 模块也需要依赖 api 模块，
 * 所以 api 模块不应该设置得过于复杂
 */
@Deprecated(
  "common 模块已向 base、utils、config 模块迁移，请依赖后者，common 不再进行使用",
  ReplaceWith(
    "dependLibBase()\n" +
      "dependLibUtils()\n" +
      "dependLibConfig()")
)
fun Project.dependLibCommon() {
  dependencies {
    "implementation"(project(LibDepend.common))
  }
}

fun Project.dependLibConfig() {
  dependencies {
    "implementation"(project(LibDepend.config))
  }
}

fun Project.dependLibUtils() {
  dependencies {
    "implementation"(project(LibDepend.utils))
  }
}