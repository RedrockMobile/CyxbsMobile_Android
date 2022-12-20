plugins {
  id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.login"

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiUpdate()

dependLottie()
dependRxjava()
dependNetwork()
dependGlide()