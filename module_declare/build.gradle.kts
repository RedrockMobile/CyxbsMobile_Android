plugins {
    id("module-debug")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependRxjava()
dependNetwork()
dependCoroutinesRx3()
dependMaterialDialog()
useDataBinding()
dependencies {
    implementation(Android.appcompat)
    implementation(Android.constraintlayout)
    implementation(Android.material)
}