import versions.eventBus
import versions.threeParty

/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation(project(":module_electricity:api_electricity"))
    implementation(project(":module_volunteer:api_volunteer"))
    implementation(project(":module_todo:api_todo"))
    implementation(project(":lib_account:api_account"))
    threeParty()
    eventBus()
}
