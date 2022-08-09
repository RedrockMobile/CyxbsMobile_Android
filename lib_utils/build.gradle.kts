import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig

plugins {
  id("module-manager")
}

dependLibCommon()
dependLibConfig()

dependApiAccount()

dependCoroutines()
dependCoroutinesRx3()
dependGlide()
dependNetwork()
dependRxjava()
dependRxPermissions()