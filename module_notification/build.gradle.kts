import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.api.dependApiMine
import com.mredrock.cyxbs.convention.depend.dependGlide
import com.mredrock.cyxbs.convention.depend.dependNetwork
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.dependWorkManger
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()

dependApiMine()

dependNetwork()
dependRxjava()
dependGlide()
dependWorkManger()

dependLibCommon()

dependencies {
    // TODO 应该替换为官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}