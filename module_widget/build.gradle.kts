import versions.defaultNet
import versions.eventBus

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    implementation(project(":module_main:api_main"))
    eventBus()
    defaultNet()
    implementation("androidx.palette:palette:1.0.0")
    implementation("com.jaredrummler:colorpicker:1.0.2")
    implementation("com.squareup.retrofit2:converter-scalars:2.7.2")
}
