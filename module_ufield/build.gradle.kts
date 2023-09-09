plugins {
    id("module-manager")
}


dependLibBase()
dependLibConfig()
dependLibUtils()

//使用ARouter
useARouter()

//网络请求的库，有retrofit，okp，gson
dependNetwork()
//rxJava
dependRxjava()

//使用smartRefreshLayout(滑动刷新要用)
dependSmartRefreshLayout()
//使用对话框
dependMaterialDialog()

//使用viewmodel livedata
dependLifecycleKtx()
//android常用库
dependAndroidView()

dependUCrop()
dependencies {
    implementation("com.contrarywind:Android-PickerView:4.1.9")
//使用smartRefreshLayout的刷新头和加载头（经典款）
    implementation("com.scwang.smart:refresh-header-classics:2.0.1")   //经典刷新头
    implementation("com.scwang.smart:refresh-footer-classics:2.0.1")  //经典加载
//一般使用depend关键字编译器会有提示，导入依赖，亦可以使用自己的依赖（一般情况下掌邮都有。在build-logic下面），如果没有请在下面添加


}


