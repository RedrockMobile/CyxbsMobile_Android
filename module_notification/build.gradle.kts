plugins {
    id("module-manager")
    id("org.jetbrains.kotlin.android")
}
dependLibBase()


dependApiAccount()

dependApiMine()
dependLibConfig()

dependLibUtils()
dependLibConfig()

dependNetwork()
dependRxjava()
dependGlide()
dependWorkManger()
dependLibUtils()
dependLibCommon()

dependencies {
    // TODO 应该替换为官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}

useARouter()