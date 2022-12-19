plugins {
    id("module-debug")
}

dependApiLogin()
dependApiAccount()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependNetwork()
dependRxjava()
dependSmartRefreshLayout()
dependMaterialDialog()
dependAutoService()

dependencies {
    implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}