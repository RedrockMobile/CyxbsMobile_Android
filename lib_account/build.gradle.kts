import versions.defaultNet
import versions.dialog

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    /*
    * 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
    * 公用库请不要添加到这里
    * */
    implementation( project(":lib_account:api_account"))
    implementation( project(":module_electricity:api_electricity"))
    implementation (project(":module_main:api_main"))
    implementation( project(":module_volunteer:api_volunteer"))
    dialog()
    defaultNet()
}