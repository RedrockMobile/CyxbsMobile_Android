


plugins {
    id("module-manager")
}

dependRxjava()
dependNetwork()
dependRoom()
dependRoomRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    // https://lbs.amap.com/api/android-location-sdk/guide/create-project/android-studio-create-project
    implementation("com.amap.api:3dmap:9.4.0")
    
    // https://github.com/koral--/android-gif-drawable
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")
}
