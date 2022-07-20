import com.mredrock.cyxbs.convention.depend.api.*
import com.mredrock.cyxbs.convention.depend.*

plugins {
    id("module-manager")
    id("kotlin-android-extensions") // todo kt 获取 View 的插件已被废弃，新模块禁止再使用！
}

dependApiAccount()
dependApiMain()
dependApiUpdate()

dependMaterialDialog()
dependGlide()
dependRxjava()
dependNetwork()
dependLPhotoPicker()
dependUCrop()

dependencies {
    // PickerView https://github.com/Bigkoo/Android-PickerView
    // TODO 该库已停止更新
    implementation("com.contrarywind:Android-PickerView:4.1.9")
    // https://github.com/kyleduo/SwitchButton
    implementation("com.kyleduo.switchbutton:library:2.1.0")
    // TODO 应该替换为官方的 ShapeableImageView 来实现圆角图片
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    implementation(Network.`logging-interceptor`)
    implementation(Network.`converter-gson`)
    implementation(Network.`adapter-rxjava3`)
}
