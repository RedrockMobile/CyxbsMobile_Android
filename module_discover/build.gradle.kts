import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiVolunteer()
dependApiTodo()
dependApiAccount()
dependApiElectricity()
dependApiSport()


dependNetwork()
dependRxjava()
dependGlide()
dependEventBus()

dependLibUtils()
dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
    // 20 级郭祥瑞封装的 Banner 库
    implementation("io.github.985892345:SlideShow:2.0.0-SNAPSHOT")
}
