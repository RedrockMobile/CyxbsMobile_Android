plugins {
    id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.update"

dependMaterialDialog()
dependNetwork()
dependRxPermissions()
dependRxjava()

dependLibUtils()

dependencies {
    implementation(Network.`converter-gson`)
    implementation(Network.`adapter-rxjava3`)
}