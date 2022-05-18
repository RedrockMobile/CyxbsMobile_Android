import versions.`androidx-appcompat`
import versions.aRouter

plugins {
    id("cyxbs.library-base")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android.lint.abortOnError = false

kapt {
    // ARouter https://github.com/alibaba/ARouter
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

dependencies {
    aRouter()
    implementation(`androidx-appcompat`)
}

