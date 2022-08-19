import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
  id("module-debug")
}

dependLibBase()
dependLibUtils()
dependLibConfig()

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
  implementation(Android.`core-ktx`)
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation("com.github.985892345:NetLayout:master-SNAPSHOT")
}