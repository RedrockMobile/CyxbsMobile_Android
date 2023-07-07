plugins {
    `kotlin-dsl`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    api(project(":core:versions"))
    implementation(project(":core:base"))
    implementation(project(":core:api"))
    implementation(project(":core:app"))
    implementation(project(":core:library"))
    implementation(project(":core:module"))

    //依赖所有:plugin模块下的所有子模块
    findProject(":plugin")!!.allprojects
        .filter {
            //不依赖plugin模块本身
            it.name != "plugin"
        }.forEach {
            implementation(
                project(":plugin:${it.name}")
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