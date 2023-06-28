plugins {
  id("module-manager")
}




dependApiAccount()
dependApiInit()

dependRxjava()

dependAutoService()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}
