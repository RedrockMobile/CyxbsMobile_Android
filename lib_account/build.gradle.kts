import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
    id("module-manager")
}

dependApiElectricity()
dependApiVolunteer()
dependApiLogin()

dependMaterialDialog()
dependNetwork()

dependLibCommon() // TODO common 模块不再使用，新模块请依赖 base 和 utils 模块

dependencies {
    implementation(Rxjava.rxjava3)
    implementation(Lifecycle.`livedata-ktx`)
}