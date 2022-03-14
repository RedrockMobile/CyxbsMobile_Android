import versions.*

plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.application")
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


        applicationId = "com.redrock.$name"

        // 添加以下两句代码，这是 photolibrary 需要设置的东西
        renderscriptTargetApi = AGP.targetSdk  //版本号请与compileSdkVersion保持一致
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = AGP.testInstrumentationRunner

        kapt {
            // ARouter https://github.com/alibaba/ARouter
            arguments {
                arg("AROUTER_MODULE_NAME", project.getName())
            }
        }

        // 秘钥文件
        //manifestPlaceholders = secret.manifestPlaceholders
        /*secret.buildConfigField.forEach({ k, v ->
            buildConfigField("String", k, v)
        })*/
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

    sourceSets {
        create("main"){
            manifest {
                srcFile("src/main/AndroidManifest.xml")
            }
        }
    }

}

dependencies {
    test()
    android()
    threeParty()
    implementation (lPhotoPicker)
    implementation(project(":lib_common"))
//     上线之前如果需要检测是否有内存泄漏，直接解除注释，然后安装debug版本的掌邮
//     就会附带一个LeakCanary的app来检测是否有内存泄漏
//        debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.2'

//     https://github.com/whataa/pandora
//     开发测试工具，如果需要解除注释
//        debugImplementation 'com.github.whataa:pandora:androidx_v2.1.0'
//        debugImplementation 'com.github.whataa:pandora-no-op:v2.1.0'

}