plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":module_todo:api_todo"))
    implementation("cn.aigestudio.wheelpicker:WheelPicker:1.1.3")
}