plugins {
    id("module-debug")
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





//一般使用depend关键字编译器会有提示，导入依赖，亦可以使用自己的依赖（一般情况下掌邮都有。在build-logic下面），请在下面添加


