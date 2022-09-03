import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.*

plugins {
  id("module-debug")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()

dependRoom()
dependRoomRxjava()
dependRxjava()
dependNetwork()
dependSmartRefreshLayout()

dependencies {
  implementation(SmartRefreshLayout.`footer-ball`)
}
