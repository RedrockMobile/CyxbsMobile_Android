@file:Suppress("UnstableApiUsage", "UNCHECKED_CAST")

import ext.get
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.kotlin
import versions.AGP

plugins {
    kotlin("android")
    id("com.android.library")
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
        // 秘钥文件
        manifestPlaceholders += (rootProject.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (rootProject.ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
            buildConfigField("String", k, v)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "${rootDir}/build_logic/proguard-rules.pro")

            signingConfig = signingConfigs.getByName("config")

            ndk {
                // 修改安装包的架构要记得同步修改上面的 Bugly 的 ndk 依赖
                abiFilters += listOf("arm64-v8a","armeabi-v7a")
            }
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