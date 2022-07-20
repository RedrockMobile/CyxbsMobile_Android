import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()

dependLottie()
dependEventBus()
dependRxjava()
dependNetwork()

// TODO 这几个依赖似乎是用于加密的，但未在项目找到使用，所以暂时注释
//dependencies {
//    implementation("com.madgag.spongycastle:core:1.58.0.0")
//    implementation("com.madgag.spongycastle:prov:1.58.0.0")
//    implementation("com.madgag.spongycastle:pkix:1.54.0.0")
//    implementation("com.madgag.spongycastle:pg:1.54.0.0")
//}
