import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()
dependApiProtocol()

dependEventBus()
dependMaterialDialog()
dependPaging()
dependPhotoView()
dependNetwork()
dependRxjava()
dependGlide()
dependLPhotoPicker()
dependRoom()
dependRoomRxjava()
dependRoomPaging()
dependUCrop()

dependencies {
    // TODO 应该替换为官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    // QQ 分享
    // https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95
    // https://wiki.connect.qq.com/%e5%88%86%e4%ba%ab%e6%b6%88%e6%81%af%e5%88%b0qq%ef%bc%88%e6%97%a0%e9%9c%80qq%e7%99%bb%e5%bd%95%ef%bc%89
    implementation("com.tencent.tauth:qqopensdk:3.52.0")
    
    // 一个文本展开与收缩的库 https://github.com/MZCretin/ExpandableTextView
    implementation("com.github.MZCretin:ExpandableTextView:v1.6.1-x")
}
