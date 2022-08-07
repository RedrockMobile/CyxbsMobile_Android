import com.mredrock.cyxbs.convention.depend.dependRxPermissions
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig

plugins {
  id("module-manager")
}

dependLibCommon()

dependLibConfig()

dependRxPermissions()