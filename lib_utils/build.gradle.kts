import config.Config

plugins {
  id("module-manager")
}



dependLibConfig()

dependApiInit()

dependCoroutines()
dependCoroutinesRx3()
dependGlide()
dependNetwork()
dependRxjava()
dependRxPermissions()

dependApiAccount()


useARouter(false) // lib_utils 模块不包含实现类，不需要处理注解


android {
  buildFeatures {
    buildConfig = true
  }
  defaultConfig {
    // 写入版本信息到 BuildConfig，其他模块可以通过调用 getAppVersionCode() 和 getAppVersionName() 方法获得
    buildConfigField("long", "VERSION_CODE", Config.versionCode.toString())
    buildConfigField("String", "VERSION_NAME", "\"${Config.versionName}\"")
  }
}