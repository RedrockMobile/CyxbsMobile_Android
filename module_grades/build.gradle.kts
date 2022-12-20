plugins {
    id("module-manager")
}


dependApiAccount()

dependLottie()
dependRoom()
dependNetwork()
dependRxjava()
dependGlide()
dependLibConfig()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    // TODO 使用 官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
