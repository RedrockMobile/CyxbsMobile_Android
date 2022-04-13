package versions

object AGP {
    const val mineSdk = 21
    const val targetSdk = 31
    const val compileSdk = targetSdk
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    const val releaseApplicationId = "com.mredrock.cyxbs"
    const val releaseVersionCode = 77 // 线上75，开发77
    const val releaseVersionName = "6.4.4" // 线上6.4.2，开发6.4.4

    val releaseAbiFilters = listOf("arm64-v8a")
}