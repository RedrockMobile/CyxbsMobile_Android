import com.android.build.gradle.internal.packaging.defaultExcludes
import ext.get
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.kotlin
import versions.AGP

plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.library")
}

apply(from="$rootDir/build_logic/dependencies.gradle")

android {
    compileSdk = AGP.compileSdk

    lint {
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
        println(ext.hashCode())
        // 秘钥文件
        manifestPlaceholders += (project.ext["secret"]["manifestPlaceholders"] as Map<String, Any>)
        (project.ext["secret"]["buildConfigField"] as Map<String,String>).forEach { (k, v) ->
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

    sourceSets {
        named("main"){
            manifest {
                srcFile("src/main/AndroidManifest.xml")
            }
            packagingOptions.setExcludes(defaultExcludes + "debug/**")
        }
    }

}

dependencies {
    /*
   * todo
   *  1、这里是给所有子模块配置依赖，包括 module_、lib_、api_
   *  2、如果只有你自己的模块需要单独的依赖，请添加到你自己模块的 build.gradle.kts 中
   *  3、如果以后引入一些特殊的依赖，比如：bugly 这种，只需要给 module_app 单独添加即可
   * */

    // ARouter https://github.com/alibaba/ARouter
    implementation ("com.alibaba:arouter-api:1.5.2")
    kapt ("com.alibaba:arouter-compiler:1.5.2")
    // 上线之前如果需要检测是否有内存泄漏，直接解除注释，然后安装debug版本的掌邮
    // 就会附带一个LeakCanary的app来检测是否有内存泄漏
//        debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.2'

    // https://github.com/whataa/pandora
    // 开发测试工具，如果需要解除注释
//        debugImplementation 'com.github.whataa:pandora:androidx_v2.1.0'
//        debugImplementation 'com.github.whataa:pandora-no-op:v2.1.0'

    implementation ("com.github.limuyang2:LPhotoPicker:2.6")

}




