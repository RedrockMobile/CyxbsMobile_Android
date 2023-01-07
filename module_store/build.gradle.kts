plugins {
    id("module-manager")
}


dependApiAccount()

dependPhotoView()
dependGlide()
dependRxjava()
dependNetwork()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependencies {
    // 20 级郭祥瑞封装的 Banner 库，如果有问题，欢迎来联系 👀
    implementation("io.github.985892345:SlideShow:2.0.1-SNAPSHOT")
}