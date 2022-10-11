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

        create("application.base") {
            implementationClass="BaseApplicationPlugin"
            id="application.base"
        }
    }
}

dependencies {
    api(project(":core:versions"))
    api("com.android.tools.build:gradle:7.2.1")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
}