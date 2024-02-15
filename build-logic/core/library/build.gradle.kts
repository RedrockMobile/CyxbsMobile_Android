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
        create("lib.base") {
            implementationClass = "LibBasePlugin"
            id = "lib.base"
        }

        create("lib.common") {
            implementationClass = "LibCommonPlugin"
            id = "lib.common"
        }

        create("lib.config") {
            implementationClass = "LibConfigPlugin"
            id = "lib.config"
        }

        create("lib.debug") {
            implementationClass = "LibDebugPlugin"
            id = "lib.debug"
        }

        create("lib") {
            implementationClass = "LibPlugin"
            id = "lib"
        }

        create("lib.single") {
            implementationClass = "LibSinglePlugin"
            id = "lib.single"
        }

        create("lib.utils") {
            implementationClass = "LibUtilsPlugin"
            id = "lib.utils"
        }
    }
}