plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_protocol:api_protocol"))
}
android.buildFeatures.dataBinding = true