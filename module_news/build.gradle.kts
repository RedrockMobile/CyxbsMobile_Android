



/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("module-manager")
}

dependMaterialDialog()
dependRxPermissions()
dependNetwork()
dependRxjava()
dependLibUtils()
dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    // TODO 应该替换为官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
