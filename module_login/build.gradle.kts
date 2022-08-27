import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.dependGlide
import com.mredrock.cyxbs.convention.depend.dependLottie
import com.mredrock.cyxbs.convention.depend.dependNetwork
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-debug")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()

dependLottie()
dependRxjava()
dependNetwork()
dependGlide()