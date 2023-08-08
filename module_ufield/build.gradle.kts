plugins {
    id("module-debug")
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


