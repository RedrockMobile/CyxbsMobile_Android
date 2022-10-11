plugins {
    `kotlin-dsl`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {

    api(project(":core:versions"))
    implementation(project(":core:base"))

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