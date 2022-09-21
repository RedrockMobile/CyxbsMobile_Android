import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.dependNetwork
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-manager")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

dependNetwork()
dependRxjava()

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
  implementation(Android.`core-ktx`)
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation(Android.cardview)
  api("io.github.985892345:NetLayout:1.0.0-SNAPSHOT")
}