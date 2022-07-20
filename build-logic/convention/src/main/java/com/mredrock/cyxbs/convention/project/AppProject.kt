@file:Suppress("UnstableApiUsage")

package com.mredrock.cyxbs.convention.project

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.project.base.BaseApplicationProject
import com.tencent.vasdolly.plugin.extension.ChannelConfigExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.*
import java.io.File

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 12:20
 */
class AppProject(project: Project) : BaseApplicationProject(project) {
  override fun initProject() {
    dependAllProject()
    dependLibCommon()
    dependBugly()
    dependSophix()
    dependUmeng()
    dependVasDolly()
    dependAutoService()
    dependRxjava()
  }
  
  /**
   * 引入第一层目录下所有的 module 和 lib 模块
   */
  private fun dependAllProject() {
    
    // 测试使用，设置 module_app 暂时不依赖的模块
    val excludeList = mutableListOf<String>(
    )
    
    // 根 gradle 中包含的所有子模块
    val includeProjects = rootProject.allprojects.map { it.name }
    
    dependencies {
      //引入所有的module和lib模块
      rootDir.listFiles()!!.filter {
        // 1.是文件夹
        // 2.不是module_app
        // 3.以lib_或者module_开头
        // 4.去掉暂时排除的模块
        // 5.根 gradle 导入了的模块
        it.isDirectory
          && it.name != "module_app"
          && "(lib_.+)|(module_.+)".toRegex().matches(it.name)
          && it.name !in excludeList
          && includeProjects.contains(it.name)
      }.forEach {
        "implementation"(project(":${it.name}"))
      }
    }
  }
  
  @Suppress("UNCHECKED_CAST")
  override fun initAndroid(extension: BaseAppModuleExtension) {
    apply(from = "$rootDir/build-logic/script/andresguard.gradle")
    apply(from = "$rootDir/build-logic/script/redex.gradle")
    apply(from = "$rootDir/build-logic/secret/secret.gradle")
    apply(plugin = "com.tencent.vasdolly")
    super.initAndroid(extension)
    extension.run {
      val ext = extensions["ext"] as ExtraPropertiesExtension
      operator fun Any?.get(key: String): Any? {
        check(this is Map<*, *>) { "key = $key，当前 key 值对应的不为 Map 类型" }
        return this.getOrDefault(key, null)
      }
      
      // channel 闭包，这是腾讯的多渠道打包
      configure<ChannelConfigExtension> {
        //指定渠道文件
        channelFile = file("channel.txt")
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
  
      signingConfigs {
        create("config") {
          // 获取保存在 secret.gradle 中的变量
          keyAlias = ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
          keyPassword = ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
          storePassword = ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
          storeFile = file("$rootDir/build-logic/secret/key-cyxbs")
        }
      }
      
      defaultConfig {
        // 秘钥文件
        manifestPlaceholders += (ext["secret"]["manifestPlaceholders"] as Map<String, String>)
        (ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
          buildConfigField("String", k, v)
        }
      }
  
      buildTypes {
        release {
          signingConfig = signingConfigs.getByName("config")
        }
        debug {
          signingConfig = signingConfigs.getByName("config")
        }
      }
    }
  }
}