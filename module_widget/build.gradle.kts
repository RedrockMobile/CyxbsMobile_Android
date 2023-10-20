plugins {
    id("module-manager")
}


dependApiAccount()
dependApiCourse()
dependApiAffair()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependRoom()
dependRxjava()
dependNetwork()
dependRoomRxjava()

useARouter()

dependencies {
    // 985892345 写的桌面小组件 https://github.com/985892345/CQUPTCourseWidget
    // 目前只实现了单个透明的小组件，后续没精力维护了，让学弟重构吧
    implementation("io.github.985892345:course-widget:0.0.1-alpha05-SNAPSHOT")
}


