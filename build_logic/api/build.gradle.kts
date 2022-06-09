plugins {
    `kotlin-dsl`
}

apply(from="$rootDir/secret/secret.gradle")

/*extra.properties.forEach { t, u ->
    println(t+u)
}*/
dependencies {
    implementation(platform("com.mredrock.team.platform:plugins"))
    implementation(project(":base"))
    implementation("com.alibaba:arouter-register")

    implementation("com.mredrock.team.platform:version")
    implementation("com.android.tools.build:gradle")

}