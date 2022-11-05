import com.mredrock.cyxbs.convention.depend.Rxjava
import com.mredrock.cyxbs.convention.depend.api.dependApiAffair
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig

plugins {
  id("module-manager")
}

dependLibConfig()
dependApiAffair()

dependencies {
  implementation(Rxjava.rxjava3)
}