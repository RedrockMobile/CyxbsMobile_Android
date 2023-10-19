plugins {
    id("module-manager")
}


dependApiVolunteer()
dependApiTodo()
dependApiAccount()
dependApiElectricity()
dependApiSport()


dependNetwork()
dependRxjava()
dependGlide()
dependEventBus()

dependLibUtils()
dependLibConfig()
dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    // 20 级郭祥瑞封装的 Banner 库，如果有问题，欢迎来联系 👀
    implementation("io.github.985892345:SlideShow:2.0.1")
}

useARouter()
