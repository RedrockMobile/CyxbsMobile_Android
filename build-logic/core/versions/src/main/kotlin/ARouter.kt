import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
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
 * 使用 ARouter
 *
 * 单独给每个模块都添加而不是直接在 build-logic 中全部添加的原因:
 * - 为了按需引入 kapt
 * - 部分 lib 模块只使用依赖，不包含注解
 *
 * @param isNeedProcessAnnotation 是否需要处理注解，对于非实现模块是不需要处理注解的，比如 api 模块
 */
fun Project.useARouter(isNeedProcessAnnotation: Boolean = !name.startsWith("api_")) {
    if (isNeedProcessAnnotation) {
        // kapt 按需引入
        apply(plugin = "org.jetbrains.kotlin.kapt")
        extensions.configure<KaptExtension> {
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
            }
        }
        dependencies {
            "kapt"(ARouter.`arouter-compiler`)
        }
    }
    dependencies {
        "implementation"(ARouter.`arouter-api`)
    }
}