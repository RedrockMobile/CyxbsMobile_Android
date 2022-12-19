




plugins {
    id("module-manager")
}

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