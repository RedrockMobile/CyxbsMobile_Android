plugins {
  id("module-debug")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiAffair()

dependRoom()
dependRoomRxjava()
dependRxjava()
dependNetwork()
dependCoroutinesRx3()

dependencies {
  // 20 级郭祥瑞封装的 Banner 库，如果有问题，欢迎来联系 👀
  implementation("io.github.985892345:SlideShow:2.0.1-SNAPSHOT")
}

