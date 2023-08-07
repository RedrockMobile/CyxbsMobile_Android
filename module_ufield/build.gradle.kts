plugins {
    id("module-manager")
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
}

