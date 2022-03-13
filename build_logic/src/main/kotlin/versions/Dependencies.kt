@file:SuppressWarnings("all")

package versions

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude

const val aRouterVersion = "1.5.2"
const val appCompat = "1.4.0"
const val lifecycle = "2.4.1"

//ARouter
const val `arouter-api` = "com.alibaba:arouter-api:$aRouterVersion"
const val `arouter-compiler` = "com.alibaba:arouter-compiler:$aRouterVersion"

//AndroidX
const val `androidx-appcompat` = "androidx.appcompat:appcompat:$appCompat"

//Test
const val `testImpl-junit` = "junit:junit:4.13.2"
const val `androidTestImpl-junit` = "androidx.test.ext:junit:1.1.3"
const val `androidTestImpl-espresso` = "androidx.test.espresso:espresso-core:3.4.0"

//Bugly
const val `bugly-native-crash-report` = "com.tencent.bugly:crashreport_upgrade:latest.release"
const val `bugly-crash-report` = "com.tencent.bugly:crashreport_upgrade:latest.release"

//美团多渠道打包
const val `walle-library` = "com.meituan.android.walle:library:1.1.7"

//umeng
const val `umeng-common` = "com.umeng.umsdk:common:9.4.4"
const val `umeng-asms` = "com.umeng.umsdk:asms:1.4.3"
const val `umeng-push` = "com.umeng.umsdk:push:6.4.0"

//Sophics
const val `sophics-android-hotfix` = "com.aliyun.ams:alicloud-android-hotfix:3.3.5"

//Rxjava3
const val `rxjava3-common` = "io.reactivex.rxjava3:rxjava:3.1.3"
const val `rxjava3-android` = "io.reactivex.rxjava3:rxandroid:3.0.0"
const val `rxjava3-kotlin` = "io.reactivex.rxjava3:rxkotlin:3.0.1"

//LphotoPicker
const val lPhotoPicker = "com.github.limuyang2:LPhotoPicker:2.6"

val android = listOf(
    "androidx.constraintlayout:constraintlayout:2.1.2",
    "androidx.recyclerview:recyclerview:1.2.1",
    "androidx.cardview:cardview:1.0.0",
    "androidx.viewpager2:viewpager2:1.0.0",
    "com.google.android.material:material:1.4.0",

    "androidx.core:core-ktx:1.7.0",
    "androidx.collection:collection-ktx:1.2.0",
    "androidx.fragment:fragment-ktx:1.4.1",
    "androidx.navigation:navigation-runtime-ktx:2.4.1",
    "androidx.navigation:navigation-fragment-ktx:2.4.1",
    "androidx.navigation:navigation-ui-ktx:2.4.1",
    "androidx.room:room-ktx:2.4.2",
    "androidx.work:work-runtime-ktx:2.7.1",
    "androidx.activity:activity-ktx:1.4.0",


    "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle",
    "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle",
    "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle",
    "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle",
    "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle",
    "androidx.lifecycle:lifecycle-common-java8:$lifecycle",
    "androidx.lifecycle:lifecycle-service:$lifecycle",
    "androidx.lifecycle:lifecycle-process:$lifecycle",
    "androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle",
)













// Bugly https://bugly.qq.com/docs/
// 其中 latest.release 指代最新 Bugly SDK 版本号
// Bugly 有如下功能：1、检测 bug；2、弹 dialog 强制用户升级
fun DependencyHandlerScope.bugly(){
    "implementation"(`bugly-crash-report`)
    "implementation"(`bugly-native-crash-report`)
}

// 友盟 https://developer.umeng.com/docs/67966/detail/206987
// 注意：请不要随意升级，请查看官方文档后进行升级（官方文档有旧文档可以进行比较），因为很可能改一些东西
// 还有，目前掌邮只使用了友盟的推送服务，按照目前（22年）的官方命名叫 U-Push
fun DependencyHandlerScope.umeng(){
    "implementation"(`umeng-asms`)// (必选)
    "implementation"(`umeng-common`)// asms包依赖必选
    "implementation"(`umeng-push`) {
        exclude(module="utdid")
    }
}

// walle https://github.com/Meituan-Dianping/walle
fun DependencyHandlerScope.walle(){
    "implementation"(`walle-library`)
}

// Sophix https://help.aliyun.com/document_detail/61082.html
// 注意：请不要随意升级，请查看官方文档后进行升级，因为很可能改一些东西
fun DependencyHandlerScope.hotFix(){
    "implementation"(`sophics-android-hotfix`)
}


fun DependencyHandlerScope.android(){
    android.forEach {
        "implementation"(it)
    }
}

fun DependencyHandlerScope.aRouter() {
    "implementation"(`arouter-api`)
    "kapt"(`arouter-compiler`)
}

fun DependencyHandlerScope.rxjava3() {
    "implementation" (`rxjava3-kotlin`)
    "implementation"(`rxjava3-common`)
    "implementation"(`rxjava3-android`)
}

fun DependencyHandlerScope.test(){
    "testImplementation"(`testImpl-junit`)
    "androidTestImplementation"(`androidTestImpl-junit`)
    "androidTestImplementation"(`androidTestImpl-espresso`)
}