import versions.defaultNet
import versions.eventBus
import versions.lottie

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation(project(":module_volunteer:api_volunteer"))
    implementation(project(":lib_account:api_account"))
    implementation("com.madgag.spongycastle:core:1.58.0.0")
    implementation("com.madgag.spongycastle:prov:1.58.0.0")
    implementation("com.madgag.spongycastle:pkix:1.54.0.0")
    implementation("com.madgag.spongycastle:pg:1.54.0.0")
    lottie()
    eventBus()
    defaultNet()
}