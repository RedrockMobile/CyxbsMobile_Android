import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig

plugins {
  id("module-manager")
}

dependLibConfig()

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation("com.github.985892345:NetLayout:master-SNAPSHOT")
}