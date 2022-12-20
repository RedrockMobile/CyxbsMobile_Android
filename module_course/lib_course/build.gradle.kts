plugins {
  id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.lib.course"

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiCourse()

dependNetwork()
dependRxjava()

dependencies {
  implementation(Android.`core-ktx`)
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation(Android.cardview)
  // 20 级郭祥瑞封装的课表底层控件，如果有问题，欢迎来联系 👀
  api("io.github.985892345:NetLayout:1.0.0")
}