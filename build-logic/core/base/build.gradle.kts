plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

gradlePlugin {
    plugins {
        create("library.base") {
            implementationClass="BaseLibraryPlugin"
            id="library.base"
        }

        create("application.base") {
            implementationClass="BaseApplicationPlugin"
            id="application.base"
        }
    }
}

dependencies {
    implementation(project(":core:versions"))
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
}