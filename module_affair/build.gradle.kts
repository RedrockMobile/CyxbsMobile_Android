plugins {
  id("module-debug")
}


dependApiLogin()
dependApiAccount()
dependApiCourse()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependRoomRxjava()
dependCoroutines()
dependRoom()
dependNetwork()
dependRxjava()

dependencies {
  // 选择器 https://github.com/wangjiegulu/WheelView
  implementation("com.github.gzu-liyujiang.AndroidPicker:WheelView:4.1.9")
}