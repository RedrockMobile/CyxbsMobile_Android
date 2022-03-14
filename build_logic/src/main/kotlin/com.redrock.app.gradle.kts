import ext.get
import versions.*

val isSingleModuleDebug: String by project

plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.application")
    id("walle")
}

apply(from = "$rootDir/build_logic/andresguard.gradle")
apply(from = "$rootDir/build_logic/redex.gradle")

android {
    defaultConfig {
        compileSdk = AGP.compileSdk
        applicationId = AGP.releaseApplicationId
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk
        versionCode = AGP.releaseVersionCode
        versionName = AGP.releaseVersionName

        ndk {
            abiFilters += AGP.releaseAbiFilters
        }

        dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }

        signingConfigs {
            create("config") {
                keyAlias = project.ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
                keyPassword = project.ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
                storePassword = project.ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
                storeFile = file("$rootDir/build_logic/key-cyxbs")
            }
        }

        buildTypes {
            debug {
                isMinifyEnabled = false
                isZipAlignEnabled = false
                isShrinkResources = false

                signingConfig = signingConfigs.getByName("config")
                ndk {
                    abiFilters+=listOf("arm64-v8a")
                }
            }

            release {
                isMinifyEnabled = true
                isZipAlignEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

                signingConfig = signingConfigs.getByName("config")

                ndk {
                    // 修改安装包的架构要记得同步修改上面的 Bugly 的 ndk 依赖
                    abiFilters += listOf("arm64-v8a")
                }
            }
        }

        dataBinding.isEnabled = true

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

        (project.ext["secret"]["buildConfigField"] as Map<String, String>).forEach{ (k, v) ->
            buildConfigField("String",k,v)
        }

    }
}

dependencies {
    //引入所有的模块
    includeAllModules()
    //引入外部所需依赖
    includeDependencies()
}

walle {
    apkOutputFolder = file("${buildDir}/outputs/channels")
    channelFile = file("$projectDir/channel")
    apkFileNameFormat = "掌上重邮-\${channel}-\${buildType}-v\${versionName}-\${versionCode}.apk"
}

fun DependencyHandlerScope.includeAllModules() {
    rootDir.listFiles()!!.filter {
        it.isDirectory
    }.filter {
        "(lib_.+)|(module_.+)".toRegex().matches(it.name)
    }.onEach {
        println(":${it.name}")
        implementation(":${it.name}")
    }
}

fun DependencyHandlerScope.includeDependencies() {
    bugly()
    rxjava3()
    umeng()
    walle()
    aRouter()
    test()
    hotFix()
}
