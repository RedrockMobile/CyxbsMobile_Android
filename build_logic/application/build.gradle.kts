plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.mredrock.team.platform:plugins"))
    implementation("com.mredrock.team.platform:version")
    implementation(project(":base"))

    implementation("com.alibaba:arouter-register")
    implementation("com.tencent.mm:AndResGuard-gradle-plugin")
    implementation("com.tencent.vasdolly:plugin")
}