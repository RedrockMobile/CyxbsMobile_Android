plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))


dependencies {
    implementation(project(":core:base"))
}


gradlePlugin {

    plugins {

    }

}
