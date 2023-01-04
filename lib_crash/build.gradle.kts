plugins {
    id("module-manager")
}
android.namespace = "com.mredrock.lib.crash"

dependLibBase()
dependLibUtils()
dependAutoService()

dependBugly()

dependencies {
    // 这里面写只有自己模块才会用到的依赖
    implementation(Android.constraintlayout)
    implementation(project(":api_init")) // 因为 api_init 没有实现模块，所以写这里
}