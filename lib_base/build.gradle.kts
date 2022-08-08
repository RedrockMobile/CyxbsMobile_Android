import com.mredrock.cyxbs.convention.depend.dependCoroutines
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-manager")
}

dependLibCommon()
dependLibUtils()

dependCoroutines()