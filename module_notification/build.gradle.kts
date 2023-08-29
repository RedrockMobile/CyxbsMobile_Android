plugins {
    id("module-debug")
}
dependLibBase()
dependLibConfig()
dependLibUtils()

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

useARouter()