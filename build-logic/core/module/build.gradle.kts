plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))


dependencies {
    implementation(project(":core:base"))
}


gradlePlugin {

    plugins {
        create("module.debug") {
            id = "module.debug"
            implementationClass = "ModuleDebugPlugin"
        }

        create("module") {
            id = "module"
            implementationClass = "ModulePlugin"
        }

    }

}
