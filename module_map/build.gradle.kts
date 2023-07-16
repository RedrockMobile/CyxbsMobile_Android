plugins {
    id("module-manager")
}


dependGlide()
dependRxjava()
dependNetwork()
dependLPhotoPicker()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    /*
    * 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
    * 公用库请不要添加到这里
    * */
    // TODO 这个是之前强神从 implementation 改成 compileOnly 的，但很奇怪的是能跑起来，应该是存在间接依赖
    compileOnly("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
}

useDataBinding()
useARouter()