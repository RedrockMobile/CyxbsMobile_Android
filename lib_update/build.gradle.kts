import versions.defaultNet
import versions.dialog
import versions.rxPermission

/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_update:api_update"))
    dialog()
    defaultNet()
    rxPermission()
}