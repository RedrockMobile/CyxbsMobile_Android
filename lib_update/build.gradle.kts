plugins {
    id("module-manager")
}


dependMaterialDialog()
dependNetwork()
dependRxPermissions()
dependRxjava()

dependLibUtils()

dependencies {
    implementation(Network.`converter-gson`)
    implementation(Network.`adapter-rxjava3`)
}

useARouter()