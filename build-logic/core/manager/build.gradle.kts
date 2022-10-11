plugins {
    `kotlin-dsl`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {

    api(project(":core:versions"))
    implementation(project(":core:base"))
    implementation(project(":core:api"))
    implementation(project(":core:app"))
    implementation(project(":core:debug"))
    implementation(project(":core:library"))
    implementation(project(":core:module"))

}

gradlePlugin {
    plugins {
        create("module-debug") {
            id = "module-debug"
            implementationClass = "ModuleDebugPlugin"
        }

        create("module-manager") {
            id = "module-manager"
            implementationClass = "ModuleManagerPlugin"
        }
    }
}