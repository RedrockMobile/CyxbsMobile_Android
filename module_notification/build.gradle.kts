plugins {
    id("module-manager")
}
android.namespace = "com.redrock.module_notification"

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