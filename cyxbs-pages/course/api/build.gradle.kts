plugins {
  id("manager.lib")
  id("kmp.compose")
}

useNavigation() // navigation 跳转

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.cyxbsComponents.view)
      implementation(projects.cyxbsComponents.utils)
      implementation(projects.cyxbsComponents.config)
    }
    androidMain.dependencies {
      // ICourseService 的旧 View 事务接口仅存在于 androidMain。
      implementation(projects.cyxbsPages.affair.api)
      implementation(libs.androidx.appcompat)
      implementation(libs.rxjava)
    }
  }
}
