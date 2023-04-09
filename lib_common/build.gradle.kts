plugins {
    id("module-manager")
}


dependApiAccount()
dependApiProtocol()
dependApiLogin()
dependApiInit()

// lib_common 默认情况下是导入所有必要的依赖
// 除了 Bugly、Sophix 等一些只需要 module_app 模块才需要
dependCoroutines()
dependCoroutinesRx3()
dependEventBus() // 避免使用 EventBus，如果需要跨模块通信，请使用 api 模块
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
dependUmeng()

