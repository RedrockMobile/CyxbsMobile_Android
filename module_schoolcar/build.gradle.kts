import versions.defaultNet

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation("com.amap.api:3dmap:9.1.0")
    implementation("com.amap.api:location:6.0.1")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")
    defaultNet()
}
