//apply(from= "$rootDir/build_logic/script/githook.gradle")
apply(from="$rootDir/build_logic/secret/secret.gradle")



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