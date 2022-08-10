import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*
import com.mredrock.cyxbs.convention.depend.lib.dependLibBase
import com.mredrock.cyxbs.convention.depend.lib.dependLibCommon
import com.mredrock.cyxbs.convention.depend.lib.dependLibConfig
import com.mredrock.cyxbs.convention.depend.lib.dependLibUtils

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()
dependApiMain()

dependNetwork()

dependLibUtils()
dependLibBase()
dependLibConfig()

dependRoom()
dependRxjava()
dependRoomRxjava()

dependencies {
    // https://developer.android.com/jetpack/androidx/releases/palette?hl=en
    implementation("androidx.palette:palette:1.0.0")
    
    // 一个颜色选择器 https://github.com/jaredrummler/ColorPicker
    implementation("com.jaredrummler:colorpicker:1.1.0")
    
    // https://github.com/square/retrofit/tree/master/retrofit-converters/scalars
    implementation("com.squareup.retrofit2:converter-scalars:${Network.retrofit_version}")
}
