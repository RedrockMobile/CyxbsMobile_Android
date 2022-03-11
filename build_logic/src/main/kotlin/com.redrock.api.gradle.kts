plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.library")
}

android {
    compileSdk = versions.AGP.compileSdk

    defaultConfig {
        minSdk = versions.AGP.mineSdk
        targetSdk = versions.AGP.targetSdk
        testInstrumentationRunner = versions.AGP.testInstrumentationRunner

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["AROUTER_MODULE_NAME"] = project.name
            }
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions.jvmTarget = "1.8"

    dataBinding.isEnabled = true
}

dependencies {
    implementation(versions.Dependencies.ARouter.api)
    implementation(versions.Dependencies.AndroidX.appcompat)
    annotationProcessor(versions.Dependencies.ARouter.compiler)
}
