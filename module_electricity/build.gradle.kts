plugins {
    id("module-manager")
}


dependApiAccount()

dependWheelPicker()
dependNetwork()
dependRxjava()
dependCoroutinesRx3()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

useARouter()

