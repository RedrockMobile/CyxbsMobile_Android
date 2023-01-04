plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}
android.namespace = "com.cyxbsmobile_single.module_todo"

dependWheelPicker()
dependNetwork()
dependRxjava()
dependRoom()
dependRoomRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块