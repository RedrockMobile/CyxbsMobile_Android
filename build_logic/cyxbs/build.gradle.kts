plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":application"))
    implementation(project(":common"))
    implementation(project(":library"))
    implementation(project(":api"))
    implementation(project(":script"))
}

