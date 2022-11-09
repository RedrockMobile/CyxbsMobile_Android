import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.dependAutoService
import com.mredrock.cyxbs.convention.depend.dependBugly
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
}

dependLibBase()
dependLibUtils()
dependAutoService()

dependBugly()

dependencies {
    // 这里面写只有自己模块才会用到的依赖
    implementation(Android.constraintlayout)
}