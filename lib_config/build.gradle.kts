import com.mredrock.cyxbs.convention.depend.Android

plugins {
  id("module-manager")
}

dependencies {
  implementation(Android.appcompat)
  implementation(Android.constraintlayout)
  implementation(Android.material)
}
