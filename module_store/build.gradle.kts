import versions.defaultNet
import versions.photoView
import versions.threeParty

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    photoView()
    threeParty()
}
android.buildFeatures.dataBinding = true
