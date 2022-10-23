plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

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
    api("com.android.tools.build:gradle:7.2.1")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
}