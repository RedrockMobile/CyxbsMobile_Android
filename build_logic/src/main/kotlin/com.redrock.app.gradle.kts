import ext.get
import versions.*

plugins {
    id("cyxbs.application-base")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.tencent.vasdolly")
}

apply(from = "$rootDir/build_logic/script/andresguard.gradle")
apply(from = "$rootDir/build_logic/script/redex.gradle")

android {
    defaultConfig {
        applicationId = AGP.releaseApplicationId
        versionCode = AGP.releaseVersionCode
        versionName = AGP.releaseVersionName

        ndk {
            abiFilters += AGP.releaseAbiFilters
        }

        /*dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }*/

        signingConfigs {
            create("config") {
                keyAlias = project.ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
                keyPassword = project.ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
                storePassword = project.ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
                storeFile = file("$rootDir/build_logic/secret/key-cyxbs")
            }
        }
        channel{
            //指定渠道文件
            channelFile = file("${rootDir}/build_logic/channel.txt")
            //多渠道包的输出目录，默认为new File(project.buildDir,"channel")
            outputDir = File(project.buildDir,"channel")
            //多渠道包的命名规则，默认为：${appName}-${versionName}-${versionCode}-${flavorName}-${buildType}-${buildTime}
            apkNameFormat ="\${appName}-\${versionName}-\${versionCode}-\${flavorName}-\${buildType}"
            //快速模式：生成渠道包时不进行校验（速度可以提升10倍以上，默认为false）
            fastMode = false
            //buildTime的时间格式，默认格式：yyyyMMdd-HHmmss
            buildTimeDateFormat = "yyyyMMdd-HH"
            //低内存模式（仅针对V2签名，默认为false）：只把签名块、中央目录和EOCD读取到内存，不把最大头的内容块读取到内存，在手机上合成APK时，可以使用该模式
            lowMemory = false
        }

        buildTypes {
            debug {
                isMinifyEnabled = false
                isZipAlignEnabled = false
                isShrinkResources = false

                signingConfig = signingConfigs.getByName("config")
                ndk {
                    abiFilters += listOf("arm64-v8a","armeabi-v7a","x86","x86_64")
                }
            }

            release {
                isMinifyEnabled = true
                isZipAlignEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "${rootDir}/build_logic/proguard-rules.pro")

                signingConfig = signingConfigs.getByName("config")

                ndk {
                    // 修改安装包的架构要记得同步修改上面的 Bugly 的 ndk 依赖
                    abiFilters += listOf("arm64-v8a","armeabi-v7a","x86","x86_64")
                }
            }
        }

        lint {
            abortOnError = false
            baseline = file("lint-baseline.xml")
            disable += listOf("TrustAllX509TrustManager")
        }

        packagingOptions {
            jniLibs.excludes +=
                listOf(
                    "lib/armeabi/libAMapSDK_MAP_v6_9_4.so",
                    "lib/armeabi/libsophix.so",
                    "lib/armeabi/libBugly.so",
                    "lib/armeabi/libpl_droidsonroids_gif.so",
                    "lib/*/libRSSupport.so",
                    "lib/*/librsjni.so",
                    "lib/*/librsjni_androidx.so"
                )
            //
            resources {
                excludes += listOf(
                    "LICENSE.txt",
                    "META-INF/DEPENDENCIES",
                    "META-INF/ASL2.0",
                    "META-INF/NOTICE",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/services/javax.annotation.processing.Processor",
                    "META-INF/MANIFEST.MF",
                    "META-INF/NOTICE.txt",
                    "META-INF/rxjava.properties"
                )
            }
        }

    }

    buildFeatures {
        dataBinding = true
    }
}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME",project.name)
    }
}

dependencies {
    projects()
    //引入外部所需依赖
    bugly()
    umeng()
    hotFix()
    aRouter()
    test()
    autoService()
    vasDolly()
}


fun DependencyHandlerScope.projects(){
    //引入所有的module和lib模块
    rootDir.listFiles()!!.filter {
        // 1.是文件夹
        // 2.不是module_app
        // 3.以lib_或者module_开头
        it.isDirectory && it.name != "module_app" && "(lib_.+)|(module_.+)".toRegex().matches(it.name)
    }.onEach {
        implementation(project(":${it.name}"))
    }

}
