import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
}

dependApiElectricity()
dependApiVolunteer()
dependApiLogin()

dependMaterialDialog()
dependNetwork()

dependLibUtils()
dependLibConfig()

dependencies {
    implementation(Rxjava.rxjava3)
}