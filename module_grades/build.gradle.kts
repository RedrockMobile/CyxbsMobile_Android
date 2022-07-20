import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.api.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()

dependLottie()
dependRoom()
dependNetwork()
dependRxjava()

dependencies {
    // TODO 使用 官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
