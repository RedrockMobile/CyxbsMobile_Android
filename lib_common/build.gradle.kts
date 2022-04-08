import versions.*

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation(project(":module_main:api_main"))
    implementation(project(":lib_account:api_account"))
    implementation(project(":lib_protocol:api_protocol"))
    lPhotoPicker()
    threeParty()
    eventBus()
    photoView()
    rxPermission()
    dialog()

    api(`rxjava3-common`)
    api(`rxjava3-kotlin`)
    api(`rxjava3-android`)
}

android.buildFeatures.dataBinding = true
