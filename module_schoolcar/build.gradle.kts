import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependRxjava()
dependNetwork()

dependencies {
    // https://lbs.amap.com/api/android-location-sdk/guide/create-project/android-studio-create-project
    implementation("com.amap.api:3dmap:latest.integration")
    implementation("com.amap.api:location:latest.integration")
    
    // https://github.com/koral--/android-gif-drawable
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")
}
