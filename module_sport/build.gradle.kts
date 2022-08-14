import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.api.dependApiLogin
import com.mredrock.cyxbs.convention.depend.dependMaterialDialog
import com.mredrock.cyxbs.convention.depend.dependNetwork
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.dependSmartRefreshLayout
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
    //id("module-debug")
}

dependNetwork()
dependRxjava()
dependSmartRefreshLayout()
dependMaterialDialog()
dependLibUtils()
dependLibBase()
dependLibConfig()
dependApiAccount()
dependApiLogin()