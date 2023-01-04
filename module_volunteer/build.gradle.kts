plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}
android.namespace = "com.mredrock.cyxbs.volunteer"

dependApiAccount()
dependApiStore()

dependLottie()
dependEventBus()
dependRxjava()
dependNetwork()
dependCoroutinesRx3()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

