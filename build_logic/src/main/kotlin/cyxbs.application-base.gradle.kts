@file:Suppress("UnstableApiUsage", "UNCHECKED_CAST")

import ext.get
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.kotlin
import versions.AGP

plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    compileSdk = AGP.compileSdk

    publishing {
        singleVariant("debug")
    }

    signingConfigs {
        create("config") {
            keyAlias = project.ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
            keyPassword = project.ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
            storePassword = project.ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
            storeFile = file("$rootDir/build_logic/secret/key-cyxbs")
        }
    }

    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk
        testInstrumentationRunner = AGP.testInstrumentationRunner
        // 秘钥文件
        manifestPlaceholders += (project.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (project.ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
            buildConfigField("String", k, v)
        }
        ndk {
            abiFilters += AGP.releaseAbiFilters
        }
        /*dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }*/
    }

    packagingOptions {
        jniLibs.excludes +=
            listOf(
                "lib/armeabi/libAMapSDK_MAP_v6_9_4.so",
                "lib/armeabi/libsophix.so",
                "lib/armeabi/libBugly.so",
                "lib/armeabi/libpl_droidsonroids_gif.so",
                "lib/*/libRSSupport.so",
                "lib/*/librsjni.so",
                "lib/*/librsjni_androidx.so"
            )

        resources {
            excludes += listOf(
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
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}