@file:Suppress("UnstableApiUsage")

//只需要kotlin-dsl依赖无需声明其他依赖项的repository
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":plugins")
include(":version")