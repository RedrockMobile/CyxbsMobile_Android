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


    signingConfigs {
        create("config") {
            keyAlias = rootProject.ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
            keyPassword = rootProject.ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
            storePassword = rootProject.ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
            storeFile = file("$rootDir/build_logic/secret/key-cyxbs")
        }
    }

    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk
        testInstrumentationRunner = AGP.testInstrumentationRunner
        versionName = AGP.releaseVersionName
        versionCode = AGP.releaseVersionCode
        applicationId = AGP.releaseApplicationId
        // 秘钥文件
        manifestPlaceholders += (rootProject.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (rootProject.ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
            buildConfigField("String", k, v)
        }
        ndk {
            abiFilters += AGP.abiFilters
        }
        resourceConfigurations += AGP.resConfigs
        /*dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }*/
    }

    packagingOptions {
        jniLibs.excludes += AGP.jniExclude
        resources.excludes += AGP.resourcesExclude
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
