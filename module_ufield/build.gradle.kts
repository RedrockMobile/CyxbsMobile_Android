plugins {
    id("module-debug")
    id("org.jetbrains.kotlin.android")
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
//使用协程
dependCoroutines()
//使用smartRefreshLayout(滑动刷新要用)
dependSmartRefreshLayout()
//使用对话框
dependMaterialDialog()
//使用room(可能要用)
dependRoom()
//使用viewmodel livedata
dependLifecycleKtx()
//android常用库
dependAndroidView()

dependUCrop()
dependencies {
    implementation("com.contrarywind:Android-PickerView:4.1.9")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//一般使用depend关键字编译器会有提示，导入依赖，亦可以使用自己的依赖（一般情况下掌邮都有。在build-logic下面），如果没有请在下面添加
//使用smartRefreshLayout的刷新头和加载头（经典款）
    implementation("com.scwang.smart:refresh-header-classics:2.0.1")   //经典刷新头
    implementation("com.scwang.smart:refresh-footer-classics:2.0.1")  //经典加载
}


