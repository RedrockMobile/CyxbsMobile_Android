import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.*

/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("module-manager")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependLibCommon()

dependApiAccount()
dependApiUpdate()
dependApiProtocol()
dependApiLogin()

dependApiCourse()
dependLibCourse()

dependLottie()
dependEventBus()
dependRxjava()
dependNetwork()
dependGlide()
dependWorkManger()

dependencies {
    implementation(Umeng.push)
}