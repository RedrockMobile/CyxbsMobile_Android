import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.*

plugins {
  id("module-manager")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiMain()

dependRoom()
dependRoomRxjava()
dependRxjava()
dependNetwork()
dependSmartRefreshLayout()

dependencies {
  implementation(SmartRefreshLayout.`header-material`)
}
