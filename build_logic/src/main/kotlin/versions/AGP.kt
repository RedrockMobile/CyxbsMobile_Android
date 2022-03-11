package versions

object AGP {
    const val mineSdk = 21
    const val targetSdk = 31
    const val compileSdk = targetSdk
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    const val releaseApplicationId = "com.mredrock.cyxbs"
    const val releaseVersionCode = 74 // 线上74，开发75
    const val releaseVersionName = "6.4.1" // 线上6.4.1，开发6.4.2

    val releaseAbiFilters = listOf("arm64-v8a")
}