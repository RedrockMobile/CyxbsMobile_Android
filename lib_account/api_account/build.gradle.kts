import com.mredrock.cyxbs.convention.depend.Coroutines
import com.mredrock.cyxbs.convention.depend.Rxjava

plugins {
    id("module-manager")
}

dependencies {
    implementation(Rxjava.rxjava3)
    implementation(Coroutines.`coroutines-rx3`)
}