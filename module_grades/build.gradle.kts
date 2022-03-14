import versions.lottie

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(project(":lib_account:api_account"))
    lottie()
}
