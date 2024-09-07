plugins {
    id("module-manager")
}


dependWheelPicker()
dependLibBase()
dependLibConfig()
dependLibUtils()
dependApiStore()
//使用ARouter
useARouter()

dependRoom()

//网络请求的库，有retrofit，okp，gson
dependNetwork()
//rxJava
dependRxjava()