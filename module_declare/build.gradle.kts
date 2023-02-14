plugins {
    id("module-debug")
    id("org.jetbrains.kotlin.android")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependRxjava()
dependNetwork()
dependCoroutinesRx3()
dependencies {
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
