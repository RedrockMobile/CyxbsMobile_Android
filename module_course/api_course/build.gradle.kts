plugins {
  id("module-manager")
}

dependLibUtils()
dependLibConfig()
dependApiAffair()

dependencies {
  implementation(Rxjava.rxjava3)
}

useARouter()