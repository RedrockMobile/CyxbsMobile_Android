plugins {
    id("module-manager")
}


dependMaterialDialog()
dependApiAccount()
dependPhotoView()
dependRxjava()
dependApiDialog()
dependLibBase()
dependLibUtils()
dependLibConfig()

dependencies {
    // rhino j2js引擎
    // 因为最新的rhino 1.7.14 使用了javax.lang.model.SourceVersion，故不支持安卓
    // 他们似乎已经修复了这个问题，但还没有发release
    // https://github.com/mozilla/rhino/issues/1149org.o
    implementation("org.mozilla:rhino:1.7.11")
    // 20 级郭祥瑞封装的 Banner 库，如果有问题，欢迎来联系 👀
    implementation("io.github.985892345:SlideShow:2.0.0")
}