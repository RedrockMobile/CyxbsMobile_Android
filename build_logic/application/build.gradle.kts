plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.mredrock.team.platform:plugins"))
    implementation("com.mredrock.team.platform:version")
    implementation(project(":base"))
}