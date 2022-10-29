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