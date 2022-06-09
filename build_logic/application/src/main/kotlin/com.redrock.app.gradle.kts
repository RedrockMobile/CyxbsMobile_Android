@file:Suppress("UnstableApiUsage")

plugins {
    id("cyxbs.application-base")
    kotlin("kapt")
    id("kotlin-android-extensions")
}


android {

    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
        disable += listOf("TrustAllX509TrustManager")
    }



    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "${rootDir}/build_logic/proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
    }
}


