import versions.defaultNet
import versions.defaultRoom

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    defaultNet()
    defaultRoom()
}
android.buildFeatures.dataBinding = true
