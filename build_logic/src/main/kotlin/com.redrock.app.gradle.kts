import versions.AGP

plugins {
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.android.application")
}

apply(plugin = "walle")
apply(from = "andresguard.gradle")
apply(from = "redex.gradle")


android {
    defaultConfig {
        compileSdk = AGP.compileSdk
        applicationId = AGP.releaseApplicationId
        minSdk = AGP.mineSdk
        targetSdk = AGP.targetSdk
        versionCode = AGP.releaseVersionCode
        versionName = AGP.releaseVersionName

        ndk {
            abiFilters.addAll(AGP.releaseAbiFilters)
        }

        dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }

        signingConfigs {
            create("config") {
                // keyAlias = ext.properties["sign"]
            }
        }

        buildTypes {
            debug {
                isMinifyEnabled = false
                isZipAlignEnabled = false
                isShrinkResources = false

                // signingConfig signingConfigs.config
                ndk {
                    abiFilters.addAll(listOf("arm64-v8a"))
                }
            }

            release {
                isMinifyEnabled = true
                isZipAlignEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

                //signingConfig signingConfigs . config

                ndk {
                    // 修改安装包的架构要记得同步修改上面的 Bugly 的 ndk 依赖
                    abiFilters.addAll(listOf("arm64-v8a"))
                }
            }
        }

        dataBinding.isEnabled = true

        lint {
            abortOnError = false
            baseline = file("lint-baseline.xml")
            disable.addAll(listOf("TrustAllX509TrustManager"))
        }

        packagingOptions {
            jniLibs.excludes.addAll(
                listOf(
                    "lib/armeabi/libAMapSDK_MAP_v6_9_4.so",
                    "lib/armeabi/libsophix.so",
                    "lib/armeabi/libBugly.so",
                    "lib/armeabi/libpl_droidsonroids_gif.so",
                    "lib/*/libRSSupport.so",
                    "lib/*/librsjni.so",
                    "lib/*/librsjni_androidx.so"
                )
            )
            //
            resources {
                excludes.addAll(
                    listOf(
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
                )
            }
        }

    }
}

///


dependencies {

    implementation(project(":lib_common"))
    implementation(project(":lib_account"))
    implementation(project(":lib_update"))
    implementation(project(":lib_protocol"))
    implementation(project(":lib_account:api_account"))
    implementation(project(":lib_protocol:api_protocol"))
    //if (!isSingleModuleDebug.toBoolean()) {
        implementation(project(":module_main"))
        implementation(project(":module_course"))
        implementation(project(":module_qa"))
        implementation(project(":module_discover"))
        implementation(project(":module_noclass"))
        implementation(project(":module_map"))
        implementation(project(":module_othercourse"))
        implementation(project(":module_calendar"))
        implementation(project(":module_electricity"))
        implementation(project(":module_todo"))
        implementation(project(":module_emptyroom"))
        implementation(project(":module_grades"))
        implementation(project(":module_news"))
        implementation(project(":module_schoolcar"))
        implementation(project(":module_volunteer"))
        implementation(project(":module_mine"))
        implementation(project(":module_widget"))
        implementation(project(":module_redrock_home"))
        implementation(project(":module_store"))
    //}
    // Bugly https://bugly.qq.com/docs/
    // 其中 latest.release 指代最新 Bugly SDK 版本号
    // Bugly 有如下功能：1、检测 bug；2、弹 dialog 强制用户升级
    implementation("com.tencent.bugly:crashreport_upgrade:latest.release")
    implementation("com.tencent.bugly:nativecrashreport:latest.release")

    // walle https://github.com/Meituan-Dianping/walle
    implementation("com.meituan.android.walle:library:1.1.7")

    // 友盟 https://developer.umeng.com/docs/67966/detail/206987
    // 注意：请不要随意升级，请查看官方文档后进行升级（官方文档有旧文档可以进行比较），因为很可能改一些东西
    // 还有，目前掌邮只使用了友盟的推送服务，按照目前（22年）的官方命名叫 U-Push
    implementation("com.umeng.umsdk:common:9.4.4")// (必选)
    implementation("com.umeng.umsdk:asms:1.4.3")// asms包依赖必选
    implementation("com.umeng.umsdk:push:6.4.0") {
        exclude(mapOf("module" to "utdid"))
    }

    // Sophix https://help.aliyun.com/document_detail/61082.html
    // 注意：请不要随意升级，请查看官方文档后进行升级，因为很可能改一些东西
    implementation("com.aliyun.ams:alicloud-android-hotfix:3.3.5")

    implementation("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    implementation("com.alibaba:arouter-api:1.5.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}



