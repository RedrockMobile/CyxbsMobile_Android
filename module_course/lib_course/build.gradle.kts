import com.mredrock.cyxbs.convention.depend.Android
import com.mredrock.cyxbs.convention.depend.api.dependApiCourse
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

dependApiCourse()

dependNetwork()
dependRxjava()

dependencies {
  implementation(Android.`core-ktx`)
  implementation(Android.constraintlayout)
  implementation(Android.viewpager2)
  implementation(Android.`fragment-ktx`)
  implementation(Android.cardview)
  // 20 级郭祥瑞封装的课表底层控件，如果有问题，欢迎来联系 👀
  api("io.github.985892345:NetLayout:1.0.0")
}