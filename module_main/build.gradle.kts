plugins {
    id("module-debug")
}

// 测试使用，设置 module_main 暂时不依赖的模块
val excludeList = mutableListOf<String>(

)
// 依赖所有模块
project.dependencies {
    // 根 gradle 中包含的所有子模块
    project.rootProject.subprojects.filter {
        it.name !in excludeList
            && it != project
            && it.name != "module_app" // module_app 依赖 module_main，而不是反向依赖
            && it.name != "lib_single" // lib_single 只跟单模块调试有关，单模块编译时单独依赖
    }.forEach {
        "api"(it)
    }
}

dependRxjava()
dependNetwork()
dependCoroutinesRx3()

useARouter()


