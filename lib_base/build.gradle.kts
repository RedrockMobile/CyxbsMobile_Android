plugins {
  id("module-manager")
}


dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiLogin()
dependApiInit()

dependCoroutines()
dependCoroutinesRx3()
dependNetwork()

dependUmeng()

useDataBinding(true) // lib_base 模块只依赖 DataBinding 但不开启 DataBinding
useARouter(false) // lib_base 模块不包含实现类，不需要处理注解
