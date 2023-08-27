import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import java.util.Locale


/**
 * 模块缓存是 985892345 写的，ZhiQiang Tu 进行了移动
 *
 *@author ZhiQiang Tu
 *@time 2022/10/11  21:17
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */
/*
* todo 模块缓存插件存在一定缺陷，目前只是简单的比较一个模块的大小是否发生改变来决定缓存，
*  但是存在上层模块未改变，但因为下层模块的改变导致缓存的上层模块出现问题。
*  正确的配置应该是检查模块之间的依赖，对于下层模块的改变需要使所有上层模块都重新缓存。
*  可以你参考这个 aar 缓存思路：https://www.bilibili.com/video/BV1MP411X7Xf/?spm_id_from=333.999.0.0
*  目前掌邮可以使用单模块，也可以提高开发速度，模块缓存暂时关闭，等有缘人来完善 :)
*  23/7/8 ——985892345
* */

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // 开启模块缓存的总开关
        val isOpenModuleCache = false
        if (!isOpenModuleCache) {
            return
        }

        with(target) {

            apply(plugin = "org.gradle.maven-publish")

            if (plugins.hasPlugin("com.android.application")) {
                extensions.configure<BaseAppModuleExtension> {
                    publishing {
                        singleVariant("debug")
                    }
                }
            } else if (plugins.hasPlugin("com.android.library")) {
                extensions.configure<LibraryExtension> {
                    publishing {
                        singleVariant("debug")
                    }
                }
            } else {
                throw RuntimeException("只允许给 application 和 library 进行缓存，如有其他模块，请额外实现逻辑！")
            }

            // 增加 cache 闭包
            val cache = extensions.create("cache", Cache::class, project)

            afterEvaluate {
                configure<PublishingExtension> {

                    publications {

                        create<MavenPublication>("moduleCache") {
                            from(components["debug"])
                        }

                        // https://docs.gradle.org/current/userguide/publishing_maven.html#header
                        repositories {
                            maven {
                                url = cache.localMavenUri
                                group = cache.localMavenGroup
                                version = cache.localMavenVersion
                                name = cache.localMavenName
                            }
                        }

                    }

                }
            }

            cache.isAllowSelfUseCache { // 允许自身使用缓存的时候
                if (isOpenModuleCache) {
                    configurations.all {
                        resolutionStrategy.dependencySubstitution.all {
                            val requested = requested
                            if (requested is ProjectComponentSelector) {
                                val projectPath = requested.projectPath
                                val otherProject = project(projectPath)
                                // 判断当前被依赖的模块是否允许用缓存替换
                                if (cache.isAllowOtherUseCache(otherProject)) {
                                    val file =
                                        (otherProject.extensions["cache"] as Cache).getLocalMavenFile()
                                    if (file.exists()) {
                                        // 存在就直接替换依赖
                                        println("正在编译的模块：${project.name}，依赖的 $projectPath 模块被替换为缓存")
                                        useTarget("${cache.localMavenGroup}:${otherProject.name}:${otherProject.version}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            val mavenName = cache.localMavenName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            val publishTaskName = "publishModuleCachePublicationTo${mavenName}Repository"

            tasks.register("cacheToLocalMaven") {
                group = "publishing"
                /*
                * module_app 因为需要依赖所有模块，但缓存是需要进行完整打包的，
                * 存在某 module 模块是单模块调试状态，这种情况下 module_app 的构建会失败
                *
                * 意思就是：缓存时不允许两个使用 application 插件的模块之间存在依赖关系
                *
                * 但是否存在互相依赖且都同时使用了 application 插件有点难判断，所以 module_app 就默认不缓存了
                * */
                if (project.name != "module_app"
                    && !gradle.startParameter.taskNames.any {
                        it == "${project.path}:assembleDebug" // 自身模块打包时不允许缓存，因为启动模块在单模块调试时经常被修改
                    }
                    && cache.deleteOldCacheIfNeedNewCache() // 这个在简单刷新 gradle 时也会执行
                ) {
                    dependsOn(publishTaskName)
                    doFirst {
                        println("正在缓存 ${project.name} 模块")
                    }
                }
            }

            if (projectDir.parentFile.name == rootDir.name) {
                /*
                * 这里有个很奇怪的问题，如果给 api 模块加上，api 模块会报错：
                * Cannot access built-in declaration 'kotlin.String'. Ensure that you have a dependency on the Kotlin standard library
                * 只要给 api 模块调用了 tasks.whenTaskAdded 就会报，
                *
                * 怀疑是 api 模块是子模块的问题，因为取消了它的缓存，但缓存文件中仍然有它，所以不能单独给它使用 tasks.whenTaskAdded ?
                * 所以只给父文件夹名字是根项目名字的模块使用
                * */

                // 目前这里原来的代码被移走了，但为了给以后的人一个提醒，所以请不要删除上面的注释！！！
            }

            if (plugins.hasPlugin("com.android.application")) {
                if (isOpenModuleCache) {
                    tasks.whenTaskAdded {
                        if (name == "assembleDebug") {
                            dependsOn(rootProject.tasks.named("cacheToLocalMaven"))
                            /*
                            * 自动缓存的基本思路：
                            * 1、在引入了 application 插件的模块使用 assembleDebug 的 gradle 任务时，关联上根模块的 cacheToLocalMaven 任务
                            * 2、根模块的 cacheToLocalMaven 任务会拉起所有模块的 cacheToLocalMaven 任务
                            * 3、每个模块的 cacheToLocalMaven 任务里面依赖了 publishModuleCachePublicationToXXXRepository（这是 maven-publish 插件的任务），
                            *    就会自动进行缓存
                            * */
                        }
                    }
                }
            }

        }

    }
}