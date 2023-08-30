plugins {
  `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.javaTarget.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = libs.versions.kotlinJvmTarget.get()
  }
}