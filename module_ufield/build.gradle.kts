plugins {
    id("module-debug")
    id("org.jetbrains.kotlin.android")
}


dependLibBase()
dependLibConfig()
dependLibUtils()
dependLibCourse()

dependApiAccount()
dependApiCourse()

//网络请求的库，有retrofit，okp，gson
dependNetwork()
//rxJava
dependRxjava()
//使用ARouter
useARouter()
dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

//一般使用depend关键字编译器会有提示，导入依赖，亦可以使用自己的依赖（一般情况下掌邮都有。在build-logic下面），请在下面添加


