plugins {
  id("module-debug")
}
android.namespace = "com.mredrock.cyxbs.affair"

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
  // 选择器
  implementation("com.github.gzu-liyujiang.AndroidPicker:WheelView:4.1.9")
}