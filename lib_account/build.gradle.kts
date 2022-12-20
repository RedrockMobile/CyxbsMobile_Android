plugins {
    id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.account"

dependApiElectricity()
dependApiVolunteer()
dependApiLogin()

dependMaterialDialog()
dependNetwork()

dependLibUtils()
dependLibConfig()

dependencies {
    implementation(Rxjava.rxjava3)
}