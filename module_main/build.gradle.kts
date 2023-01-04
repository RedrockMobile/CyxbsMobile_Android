plugins {
    id("module-manager")
}
android.namespace = "com.mredrock.cyxbs.main"

dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiUpdate()
dependApiLogin()
dependApiAffair()

dependApiCourse()
dependLibCourse() // 需要它的背景图

dependRxjava()
dependNetwork()
dependCoroutinesRx3()

dependencies {
    implementation(Umeng.push)
}