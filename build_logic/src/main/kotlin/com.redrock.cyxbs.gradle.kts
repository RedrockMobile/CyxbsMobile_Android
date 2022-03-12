val projectName: String = project.name

when {
    "module_app".toRegex().matches(projectName) -> {
        apply(plugin = "com.redrock.app")
    }
    "module_.+".toRegex().matches(projectName) -> {
        apply(plugin = "com.redrock.module")
    }
    "lib_.+".toRegex().matches(projectName) -> {
        apply(plugin = "com.redrock.library")
    }
    "api_.+".toRegex().matches(projectName) -> {
        apply(plugin = "com.redrock.api")
    }
    else -> {
        logger.log(LogLevel.ERROR,"出现未知类型模块:$projectName $projectDir\n请为该模块声明对应的依赖插件")
    }
}