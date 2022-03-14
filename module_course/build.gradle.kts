import versions.defaultRoom
import versions.eventBus
import versions.photoView
import versions.roomBase

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    implementation(project(":module_main:api_main"))
    implementation("com.super_rabbit.wheel_picker:NumberPicker:1.0.1")
    implementation("com.umeng.umsdk:common:9.1.0")
    defaultRoom()
    eventBus()
}