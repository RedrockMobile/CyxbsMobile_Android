import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.api.dependApiLogin
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-debug")
}

dependApiLogin()
dependApiAccount()

dependLibBase()
dependLibUtils()
dependLibConfig()

dependNetwork()
dependRxjava()
dependSmartRefreshLayout()
dependMaterialDialog()
dependAutoService()