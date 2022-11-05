import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig

plugins {
  id("module-manager")
}

dependLibCommon()
dependLibConfig()

dependCoroutines()
dependCoroutinesRx3()
dependGlide()
dependNetwork()
dependRxjava()
dependRxPermissions()