import versions.*

plugins {
    id("com.redrock.cyxbs")
    id("com.tencent.vasdolly")
}

apply(plugin = "script.apk-opt")

//bytex目前不兼容AGP7.0
//apply(plugin = "bytex")

//configure<ByteXExtension> {
//    enable(true)
//    enableInDebug(false)
//    logLevel("DEBUG")
//}


android {

    //lint提速
    lint {
        checkDependencies = true
    }

}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", name)
    }
}

channel {
    //指定渠道文件
    channelFile = file("${rootDir}/build_logic/channel.txt")
    //多渠道包的输出目录，默认为new File(project.buildDir,"channel")
    outputDir = File(project.buildDir, "channel")
    //多渠道包的命名规则，默认为：${appName}-${versionName}-${versionCode}-${flavorName}-${buildType}-${buildTime}
    apkNameFormat = "\${appName}-\${versionName}-\${versionCode}-\${flavorName}-\${buildType}"
    //快速模式：生成渠道包时不进行校验（速度可以提升10倍以上，默认为false）
    fastMode = false
    //buildTime的时间格式，默认格式：yyyyMMdd-HHmmss
    buildTimeDateFormat = "yyyyMMdd-HH"
    //低内存模式（仅针对V2签名，默认为false）：只把签名块、中央目录和EOCD读取到内存，不把最大头的内容块读取到内存，在手机上合成APK时，可以使用该模式
    lowMemory = false
}

//apply(from = "$rootDir/build_logic/script/andresguard.gradle")
//apply(from = "$rootDir/build_logic/script/redex.gradle")

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

    //CodeLocator
    //官方文档https://github.com/bytedance/CodeLocator
    codeLocator()

    //上线之前如果需要检测是否有内存泄漏，直接解除注释，然后安装debug版本的掌邮
    //就会附带一个LeakCanary的app来检测是否有内存泄漏
    leakCanary()
}


fun DependencyHandlerScope.projects() {
    //引入所有的module和lib模块
    rootDir.listFiles()!!.filter {
        // 1.是文件夹
        // 2.不是module_app
        // 3.以lib_或者module_开头
        it.isDirectory
    }.filter {
        it.name != "module_app"
    }.filter {
        "(module_.+)|(lib_.+)".toRegex().matches(it.name)
    }.onEach {
        implementation(project(":${it.name}"))
    }

}
