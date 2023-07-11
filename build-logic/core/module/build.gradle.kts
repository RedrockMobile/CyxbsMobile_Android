plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

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
