pluginManagement {
    includeBuild("build_logic")
    includeBuild("build-logic")
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }

        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 以下两行代码相当于有了 google() jcenter() mavenCentral()，使用国内的阿里镜像
        maven { url = uri("$rootDir/maven") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }

        google()
        mavenCentral()
    }
}

//对文件夹进行遍历，深度为2
rootDir.walk()
    .maxDepth(2)
        //过滤掉干扰文件夹
    .filter {
        val isDirectory = it.isDirectory
        val isSubModule = file("$it/build.gradle").exists() || file("$it/build.gradle.kts").exists()
        val isIndependentProject = file("$it/settings.gradle").exists() || file("$it/settings.gradle.kts").exists()
        isDirectory  && isSubModule && !isIndependentProject
    }
    //对module进行过滤
    .filter {
        "(api_.+)|(module.+)|(lib.+)".toRegex().matches(it.name)
    }
    //将file映射到相对路径
    .map {
        when {
            it.name.startsWith("api_") -> {
                val parentName = it.parentFile.name
                val selfName = it.name
                ":$parentName:$selfName"
            }
            else -> {
                ":${it.name}"
            }
        }
    }
        //进行include
    .forEach {
        include(it)
    }
