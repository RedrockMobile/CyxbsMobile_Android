plugins {
    `kotlin-dsl`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

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
    }
}