plugins {
  id("module-manager")
}


dependLibConfig()
dependApiAffair()

dependencies {
  implementation(Rxjava.rxjava3)
}