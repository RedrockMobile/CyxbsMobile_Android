plugins {
    id("module-debug")
}

// 如果该模块要使用网络请求，就调用该方法
dependNetwork()
dependLibBase()
dependLibUtils()
dependRxjava()


dependencies {

}