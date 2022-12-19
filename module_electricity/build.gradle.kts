



/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("module-manager")
}

dependApiAccount()

dependWheelPicker()
dependNetwork()
dependRxjava()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

