plugins {
  id("module-manager")
}




dependApiAccount()
dependApiInit()

dependRxjava()
dependMaterialDialog() // 因为要设置 MaterialDialog 主题所以依赖

dependAutoService()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}

useARouter()
