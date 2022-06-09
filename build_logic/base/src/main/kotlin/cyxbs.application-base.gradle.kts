@file:Suppress("UnstableApiUsage", "UNCHECKED_CAST")

import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.kotlin
import versions.AGP

plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    compileSdk = AGP.compileSdk

    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk
        testInstrumentationRunner = AGP.testInstrumentationRunner
        versionName = AGP.releaseVersionName
        versionCode = AGP.releaseVersionCode
        applicationId = AGP.releaseApplicationId
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
