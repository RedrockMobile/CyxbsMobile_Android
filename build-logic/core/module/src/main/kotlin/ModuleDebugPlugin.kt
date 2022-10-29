import api.utils.ApiDependUtils
import com.mredrock.cyxbs.convention.depend.dependAndroidKtx
import com.mredrock.cyxbs.convention.depend.dependAndroidView
import com.mredrock.cyxbs.convention.depend.dependLifecycleKtx
import com.mredrock.cyxbs.convention.depend.lib.LibDepend
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  16:49
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ModuleDebugPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        //check
        if (plugins.hasPlugin("com.android.library")) {
            throw RuntimeException("开启单模块调试前，请先注释多模块插件！")
        }

        if (!gradle.startParameter.taskNames.any { it.contains("Release") }) {
            apply(plugin = "pandora-plugin")
        }

        apply(plugin = "base.application")


        androidApp {
            // 设置 debug 的源集
            sourceSets {
                getByName("main") {
                    /*
                    * 重定向 AndroidManifest 文件和 java 代码
                    * 以后统一将 debug 用到的 java 代码和 AndroidManifest 文件放在 main/debug 下
                    * */
                    manifest.srcFile("src/main/debug/AndroidManifest.xml")
                    java {
                        srcDir("src/main/debug")
                    }
                }
            }
        }

        dependencies {
            "debugImplementation"(project(LibDepend.debug))
        }

        dependAndroidView()
        dependAndroidKtx()
        dependLifecycleKtx()

        passOnApiImplDepend(project, project, hashSetOf(), hashSetOf())


    }

    /**
     * 单模块调试需要反向依赖 api 模块的实现模块，不然 ARouter 会报空指针
     *
     * 但存在 单模块A 依赖了 模块B，模块B 依赖了 api模块C，这种间接依赖 api 模块也要进行处理，不然也会报空指针
     *
     * 所以该方法就是为了进行递归遍历整个依赖树，将所有间接或直接的 api 依赖都同时依赖上该 api 的实现模块
     *
     * 但 api 的实现模块是不能直接得到了，为了解决这个问题，需要各位遵守规范 !!!!!
     * 将所有的 api 依赖及实现模块统一写在 [ApiDepend] 中
     *
     * @param debugProject 当前处于单模块调试的模块
     * @param project [debugProject] 间接或直接依赖的模块
     * @param dependPathSet 已经反向依赖了的 [project] 的 path 集合
     * @param recursionNameSet 已经处理了的 [project] 的 name 集合
     */
    private fun passOnApiImplDepend(
      debugProject: Project,
      project: Project,
      dependPathSet: MutableSet<String>,
      recursionNameSet: MutableSet<String>
    ) {
        recursionNameSet.add(project.name)
        if (debugProject === project) {
            println("检测到 ${debugProject.name} 开启了单模块调试，将会反向依赖 api 实现模块")
        }
        project.configurations.all {
            // all 方法是一种观察性的回调，它会把已经添加了的和之后将要添加的都进行回调
            val configuration = this
            if (configuration.name.matches(Regex("\\w*([iI]mplementation|[aA]pi|[rR]untimeOnly)\\w*"))) {
                // 只匹配带有 implementation、api、runtimeOnly 关键字配置
                configuration.dependencies.all {
                    val dependency = this
                    if (dependency is ProjectDependency) {
                        // 如果依赖的是一个项目
                        when {
                            dependency.name.startsWith("api_") -> {
                                // 对于单模块调试，需要反向依赖 api 的实现模块，不然 ARouter 无法找到，会报空指针
                                val apiPath = dependency.dependencyProject.path
                                ApiDependUtils.apiWithImplMap[apiPath]?.dependApiImplOnly(
                                    debugProject
                                ) {
                                    (!dependPathSet.contains(it) && it != debugProject.path).apply {
                                        if (this) {
                                            /*
                                            * 因为在 runtimeOnly 新依赖时又会回调上面的 configuration.dependencies.all
                                            * 所以会比下面紧接着的 forEach 调用前，再引入刚才的新依赖，所以需要在这里 add，防止重复引入
                                            * */
                                            dependPathSet.add(it)
                                        }
                                    }
                                }?.forEach {
                                    println(
                                        "${debugProject.name} 间接或直接依赖了 ${project.name}\t" +
                                                "该模块依赖了: ${dependency.name}\t\t" +
                                                "已反向依赖: $it"
                                    )
                                }
                            }
                            dependency.name.matches(Regex("module_.+|lib_.+")) -> {
                                if (!recursionNameSet.contains(dependency.name)) {
                                    // 递归寻找 api 依赖
                                    passOnApiImplDepend(
                                        debugProject,
                                        dependency.dependencyProject,
                                        dependPathSet,
                                        recursionNameSet
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}