plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.targetVersion.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlin.jvmTargetVersion.get()
    }
}

dependencies {

    implementation(project(":core:base"))

}

gradlePlugin {
    plugins {
        create("api") {
            implementationClass ="ApiPlugin"
            id="api"
        }
    }
}