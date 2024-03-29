
plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("com.mredrock.team.cache"){
            implementationClass = "PublishPlugin"
            id = "com.mredrock.team.cache"
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.javaTarget.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlinJvmTarget.get()
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}