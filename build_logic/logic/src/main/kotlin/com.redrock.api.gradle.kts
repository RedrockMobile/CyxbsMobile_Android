plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.library")
}

apply(plugin="kotlin-android-extensions")


android {
    compileSdk = versions.AGP.compileSdk
    defaultConfig {
        minSdk = versions.AGP.mineSdk
        targetSdk = versions.AGP.targetSdk
        testInstrumentationRunner = versions.AGP.testInstrumentationRunner
        kapt {
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(versions.Dependencies.ARouter.compiler)
    implementation(versions.Dependencies.AndroidX.appcompat)
    kapt(versions.Dependencies.ARouter.compiler)
}
