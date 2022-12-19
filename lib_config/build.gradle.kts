




plugins {
  id("module-manager")
}

dependLibCommon()

dependApiAccount()

dependRxjava()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}
