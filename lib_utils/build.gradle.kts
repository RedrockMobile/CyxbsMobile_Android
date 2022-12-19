plugins {
  id("module-manager")
}

dependLibCommon()
dependLibConfig()

dependCoroutines()
dependCoroutinesRx3()
dependGlide()
dependNetwork()
dependRxjava()
dependRxPermissions()

dependApiAccount()

dependAutoService()

dependencies {
  implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}