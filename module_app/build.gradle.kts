plugins {
    id ("module-manager")
}

dependRxjava()
dependAutoService()

useARouter(false) // module_app 模块不包含实现类，不需要处理注解
useDataBinding()

dependencies {
    // module_main 模块去依赖了其他模块，所以这里只依赖 module_main
    "implementation"(project(":module_main"))
}