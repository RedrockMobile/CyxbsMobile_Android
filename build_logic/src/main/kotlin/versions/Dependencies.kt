package versions

const val ARouterVersion = "1.5.2"
const val AppCompat = "1.4.0"

object Dependencies {

    object ARouter {
        const val api = "com.alibaba:arouter-api:$ARouterVersion"
        const val compiler = "com.alibaba:arouter-compiler:$ARouterVersion"
    }


    object AndroidX {
       const val appcompat = "androidx.appcompat:appcompat:$AppCompat"
    }
}