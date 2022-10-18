plugins {
    `kotlin-dsl`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {

    api(project(":core:versions"))
    implementation(project(":core:base"))
    implementation(project(":core:api"))
    implementation(project(":core:app"))
    implementation(project(":core:library"))
    implementation(project(":core:module"))

    //依赖所有:plugin:xxx project
    findProject(":plugin")!!.allprojects
        .filter {
            it.name != "plugin"
        }.forEach {
            implementation(
                group = it.group.toString(),
                name = it.name,
                version = it.version.toString()
            )
        }
}

gradlePlugin {
    plugins {
        create("module-debug") {
            id = "module-debug"
            implementationClass = "ModuleDebugManagerPlugin"
        }

        create("module-manager") {
            id = "module-manager"
            implementationClass = "ModuleManagerPlugin"
        }
    }
}