plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

gradlePlugin {
    plugins {
        create("base.library") {
            implementationClass="BaseLibraryPlugin"
            id="base.library"
        }

        create("base.application") {
            implementationClass="BaseApplicationPlugin"
            id="base.application"
        }

        create("base.android") {
            implementationClass="BaseAndroidPlugin"
            id="base.android"
        }
    }
}

dependencies {
    api(project(":core:versions"))
    api(libs.android.gradlePlugin)
    api(libs.kotlin.gradlePlugin)
}