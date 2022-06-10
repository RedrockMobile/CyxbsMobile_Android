//apply(from= "$rootDir/build_logic/script/githook.gradle")
apply(from="$rootDir/build_logic/secret/secret.gradle")

buildscript {

    repositories {
        //国内镜像等。
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://artifact.bytedance.com/repository/byteX/") }
        //国外镜像仓库
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(platform("com.mredrock.team.platform:plugins"))
        classpath("com.android.tools.build:gradle")
        classpath("com.tencent.vasdolly:plugin")
        //classpath("com.bytedance.android.byteX:base-plugin")
    }
}


val ignoreModuleMode:String by project
val ignoreModule:String by project
val isFullModuleDebug:String by project


//缓存除module_app以及gradle.properties声明外的所有模块。
tasks.register("cache"){
    group = "publishing"
    subprojects
        //去除module_app
        .filter { it.name != "module_app" }
        .filter { it.plugins.hasPlugin("com.android.library") }
        //去除gradle.properties文件声明的模块
        .filter {
            when(ignoreModuleMode){
                "normal"->it.name !in ignoreModule.split(' ')
                "regex"->!ignoreModule.toRegex().matches(it.name)
                else -> throw kotlin.IllegalStateException("目前只支持normal以及regex匹配方式")
            }
        }
        //拿到对应的发布任务
        .map { it.tasks.named("publishModuleCachePublicationToMavenRepository") }
        //依赖
        .run {
            dependsOn(this)
        }
}