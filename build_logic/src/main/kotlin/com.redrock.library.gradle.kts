import ext.get
import gradle.kotlin.dsl.accessors._5f1ca56463bf479f6b23e95385818fdf.implementation
import versions.*

plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.library")
}


android {
    compileSdk = AGP.compileSdk

    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }

    packagingOptions {
        jniLibs {

        }
    }


    defaultConfig {
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk

        // 添加以下两句代码，这是 photolibrary 需要设置的东西
        renderscriptTargetApi = AGP.targetSdk  //版本号请与compileSdkVersion保持一致
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = AGP.testInstrumentationRunner

        kapt {
            // ARouter https://github.com/alibaba/ARouter
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
            }
        }

        // 秘钥文件
        manifestPlaceholders += project.ext["secret"]["manifestPlaceholders"] as Map<String,String>
        (project.ext["secret"]["buildConfigField"] as Map<String,String>).forEach{ (k, v) ->
            buildConfigField("String",k,v)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    //threeParty()
    android()
    test()
    aRouter()
    //implementation(lPhotoPicker)
    if (project.name!="lib_common"){
        implementation(project(":lib_common"))
    }
}

