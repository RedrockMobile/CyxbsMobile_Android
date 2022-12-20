plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}
android.namespace = "com.mredrock.cyxbs.common"

dependApiAccount()
dependApiProtocol()
dependApiLogin()

// lib_common 默认情况下是导入所有必要的依赖
// 除了 Bugly、Sophix 等一些只需要 module_app 模块才需要
dependCoroutines()
dependCoroutinesRx3()
dependEventBus()
dependGlide()
dependLottie()
dependLPhotoPicker()
dependMaterialDialog()
dependNetwork()
dependPaging()
dependPhotoView()
dependRoom()
dependRoomRxjava()
dependRoomPaging()
dependRxjava()
dependRxPermissions()

dependAutoService()

dependencies {
    implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}

