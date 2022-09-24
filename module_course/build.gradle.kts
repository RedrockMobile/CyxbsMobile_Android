import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()
dependApiMain()
dependApiWidget()

dependRoom()
dependRoomRxjava()
dependRxjava()
dependEventBus()
dependNetwork()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    // TODO 未知项目，github 上未找到
    implementation("com.super_rabbit.wheel_picker:NumberPicker:1.0.1")
    implementation(Umeng.common)
    implementation(Coroutines.`coroutines-rx3`)
}
