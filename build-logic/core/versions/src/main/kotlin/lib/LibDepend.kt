import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 为了统一模块依赖，所以写了这个类
 *
 * 注意: 该类不建议有包名，因为不写包名可以不用导包
 *
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
  *
  * 写了后会由一个 gradle 脚本自动生成对应 dependLib*() 方法
  * */
  
  const val base = ":lib_base"
  const val config = ":lib_config"
  const val utils = ":lib_utils"
  const val debug = ":lib_debug"
  const val course = ":module_course:lib_course"
}

/**
 * 由于脚本不能添加 Deprecated，所以这里单独写 dependLibCommon()
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
    "implementation"(project(":lib_common"))
  }
}