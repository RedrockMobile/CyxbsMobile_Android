import com.mredrock.cyxbs.convention.depend.Rxjava
import com.mredrock.cyxbs.convention.depend.api.dependApiAffair

plugins {
  id("module-manager")
}

dependApiAffair()

dependencies {
  implementation(Rxjava.rxjava3)
}