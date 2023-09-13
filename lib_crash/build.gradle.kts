plugins {
    id("module-manager")
}

dependLibBase()
dependLibUtils()
dependLibConfig()
dependApiInit()

dependBugly()

dependencies {
    // 这里面写只有自己模块才会用到的依赖
    implementation(Android.constraintlayout)
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")//用于序列化Throwable
}

useARouter()