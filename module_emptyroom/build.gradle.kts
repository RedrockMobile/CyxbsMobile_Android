plugins {
    id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.discover.emptyroom"

dependNetwork()
dependRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块