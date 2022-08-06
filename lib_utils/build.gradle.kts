import com.mredrock.cyxbs.convention.depend.dependRxPermissions
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
  id("module-manager")
}

dependLibCommon()

dependRxPermissions()