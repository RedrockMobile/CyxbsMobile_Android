plugins {
  id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.api.course"

dependLibConfig()
dependApiAffair()

dependencies {
  implementation(Rxjava.rxjava3)
}