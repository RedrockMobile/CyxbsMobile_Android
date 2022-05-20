import versions.*

plugins {
    id("cyxbs.library-base")
    kotlin("kapt")
    id("kotlin-android-extensions")
}


android {

    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }


    defaultConfig {
        // 添加以下两句代码，这是 photolibrary 需要设置的东西
        renderscriptTargetApi = AGP.targetSdk  //版本号请与compileSdkVersion保持一致
        renderscriptSupportModeEnabled = true
    }

}

dependencies {
    //threeParty()
    androidDependency()
    test()
    aRouter()
    implementation(project(":lib_common"))
}

kapt {
    // ARouter https://github.com/alibaba/ARouter
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

