import versions.dialog
import versions.lPhotoPicker
import versions.threeParty

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation(project(":lib_account:api_account"))
    implementation(project(":module_main:api_main"))
    implementation(project(":lib_update:api_update"))

    //PickerView
    implementation("com.contrarywind:Android-PickerView:4.1.9")
    implementation("com.kyleduo.switchbutton:library:2.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.yalantis:ucrop:2.2.1")

    dialog()

    threeParty()
    lPhotoPicker()
}

android.buildFeatures.dataBinding = true
