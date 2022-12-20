plugins {
    id ("module-manager")
}
android.namespace = "com.mredrock.cyxbs"

dependApiAccount()
dependLibUtils()

dependAutoService()
dependRxjava()

dependencies {
    implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}