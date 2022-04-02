import ext.get
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project
import versions.AGP
import versions.aRouter
import versions.android
import versions.test

plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    compileSdk = AGP.compileSdk

    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk

        testInstrumentationRunner = AGP.testInstrumentationRunner
        // 秘钥文件
        manifestPlaceholders += (project.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (project.ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
            buildConfigField("String", k, v)
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}