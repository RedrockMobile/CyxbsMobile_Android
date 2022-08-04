import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.api.*

plugins {
    id("module-manager")
}

dependApiAccount()

dependLottie()
dependRoom()
dependNetwork()
dependRxjava()
dependGlide()

dependencies {
    // TODO 使用 官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
