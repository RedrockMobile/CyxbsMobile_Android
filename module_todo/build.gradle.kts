plugins {
    id("module-manager")
}


dependWheelPicker()
dependNetwork()
dependRxjava()
dependRoom()
dependRoomRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块
dependLibUtils()

useARouter()
dependLibBase()
dependLibConfig()
