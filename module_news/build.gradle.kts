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
    implementation("de.hdodenhof:circleimageview:3.1.0")
    dialog()
    rxPermission()
}
