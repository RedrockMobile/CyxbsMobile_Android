plugins {
    id("module-manager")
}


dependNetwork()
dependRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块
dependLibUtils()

useARouter()