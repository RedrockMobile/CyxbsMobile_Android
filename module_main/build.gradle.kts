plugins {
    id("module-manager")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiUpdate()
dependApiLogin()
dependApiAffair()
dependApiCrash()

dependApiCourse()
dependLibCourse() // 需要它的背景图

dependRxjava()
dependNetwork()
dependCoroutinesRx3()

useARouter()