





plugins {
    id("module-manager")
}

dependLibBase()
dependLibUtils()
dependAutoService()

dependBugly()

dependencies {
    // 这里面写只有自己模块才会用到的依赖
    implementation(Android.constraintlayout)
}