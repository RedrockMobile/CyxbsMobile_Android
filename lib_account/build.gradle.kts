plugins {
    id("module-manager")
}

dependApiLogin()

dependMaterialDialog()
dependNetwork()

dependLibUtils()
dependLibConfig()

dependencies {
    implementation(Rxjava.rxjava3)
}

useARouter()