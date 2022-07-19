import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
}

dependApiAccount()
dependApiElectricity()
dependApiMain()
dependApiVolunteer()

dependMaterialDialog()
dependNetwork()