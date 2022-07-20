import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependNetwork()
dependRxjava()