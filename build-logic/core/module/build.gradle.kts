plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.javaTarget.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlinJvmTarget.get()
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
