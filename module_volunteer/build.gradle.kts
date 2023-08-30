plugins {
    id("module-manager")
}


dependApiInit()
dependApiAccount()
dependApiStore()

dependLottie()
dependEventBus()
dependRxjava()
dependNetwork()
dependCoroutinesRx3()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

useARouter()

