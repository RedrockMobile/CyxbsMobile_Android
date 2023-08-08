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

//一般使用depend关键字编译器会有提示，导入依赖，亦可以使用自己的依赖（一般情况下掌邮都有。在build-logic下面），请在下面添加


