import com.mredrock.cyxbs.convention.depend.api.dependApiAccount
import com.mredrock.cyxbs.convention.depend.api.dependApiLogin
import com.mredrock.cyxbs.convention.depend.dependCoroutines
import com.mredrock.cyxbs.convention.depend.dependCoroutinesRx3
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-manager")
}

dependLibCommon()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiLogin()

dependCoroutines()
dependCoroutinesRx3()
