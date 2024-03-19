plugins {
    id("module-debug")
}


dependLibBase()
dependLibUtils()
dependLibConfig()

dependApiAccount()
dependApiUpdate()
dependApiLogin()
dependApiAffair()
dependApiCrash()

dependApiCourse()
dependLibCourse() // 需要它的背景图

dependRxjava()
dependNetwork()
dependCoroutinesRx3()

useARouter()



if (project.plugins.hasPlugin("com.android.application")) {
    // 如果 main 模块以单模块的形式编译，则对其他模块进行依赖
    AppPlugin.dependAllProject(project, "module_app")
}
