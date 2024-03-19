plugins {
    id ("module-manager")
}

dependRxjava()
dependAutoService()

useARouter(false) // module_app 模块不包含实现类，不需要处理注解
useDataBinding()