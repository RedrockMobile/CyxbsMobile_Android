plugins {
  id("module-manager")
}


dependLibCommon()

dependApiAccount()
dependApiInit()

dependRxjava()

dependAutoService()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}
