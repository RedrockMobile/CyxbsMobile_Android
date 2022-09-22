import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.dependRxjava
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon

plugins {
  id("module-manager")
}

dependLibCommon()

dependRxjava()

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}
