import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
}

dependApiAccount()

dependPhotoView()
dependGlide()
dependRxjava()
dependNetwork()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependencies {
    // 20 级郭祥瑞封装的 Banner 库，如果有问题，欢迎来联系 👀
    implementation("io.github.985892345:SlideShow:2.0.0")
}
