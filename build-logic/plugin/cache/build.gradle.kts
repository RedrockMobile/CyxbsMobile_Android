
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

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}