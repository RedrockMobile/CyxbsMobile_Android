plugins {
  id("module-manager")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiCourse()

dependNetwork()
dependRxjava()

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
  implementation(Android.`core-ktx`)
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation(Android.cardview)
  // 20 级郭祥瑞封装的课表底层控件，如果有问题，欢迎来联系 👀
  api("io.github.985892345:NetLayout:1.1.0-SNAPSHOT")
}