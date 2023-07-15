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

        create("lib.utils") {
            implementationClass = "LibUtilsPlugin"
            id = "lib.utils"
        }
    }
}