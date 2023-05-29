import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 *
 * ARouter 的 gradle 插件编译失败，所以取消引入，也就意味着路由采用运行时加载的方式
 * ARouter 采取扫描 dex 下所有 class 的方式加载路由，严重影响升级后的第一次启动速度
 * 加上 ARouter 不再维护，建议向 Component 移植：https://github.com/xiaojinzi123/KComponent
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:45
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object ARouter {
    // https://github.com/alibaba/ARouter
    const val arouter_version = "1.5.2"

    const val `arouter-api` = "com.alibaba:arouter-api:$arouter_version"
    const val `arouter-compiler` = "com.alibaba:arouter-compiler:$arouter_version"
}

/**
 * 所有使用 build_logic 插件的模块都默认依赖了 ARouter
 */
fun Project.dependARouter() {
    extensions.configure<KaptExtension> {
        arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }
    dependencies {
        "implementation"(ARouter.`arouter-api`)
        "kapt"(ARouter.`arouter-compiler`)
    }
}