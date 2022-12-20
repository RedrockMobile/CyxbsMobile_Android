plugins {
  id("module-manager")
}


dependLibCommon()

dependApiAccount()

dependRxjava()

dependAutoService()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
  implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}
