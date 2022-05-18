package versions

object AGP {
    const val mineSdk = 21
    const val targetSdk = 31
    const val compileSdk = targetSdk
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    const val releaseApplicationId = "com.mredrock.cyxbs"
    const val releaseVersionCode = 78 // 线上75，开发78
    const val releaseVersionName = "6.4.5" // 线上6.4.2，开发6.4.5

    val abiFilters = listOf("arm64-v8a", "armeabi-v7a")
    val resourcesExclude = listOf(
        "LICENSE.txt",
        "META-INF/DEPENDENCIES",
        "META-INF/ASL2.0",
        "META-INF/NOTICE",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/services/javax.annotation.processing.Processor",
        "META-INF/MANIFEST.MF",
        "META-INF/NOTICE.txt",
        "META-INF/rxjava.properties"
    )

    val jniExclude = listOf(
        "lib/armeabi/libAMapSDK_MAP_v6_9_4.so",
        "lib/armeabi/libsophix.so",
        "lib/armeabi/libBugly.so",
        "lib/armeabi/libpl_droidsonroids_gif.so",
        "lib/*/libRSSupport.so",
        "lib/*/librsjni.so",
        "lib/*/librsjni_androidx.so"
    )

}