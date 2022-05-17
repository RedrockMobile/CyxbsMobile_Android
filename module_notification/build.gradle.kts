import versions.threeParty

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    implementation(project(":module_mine"))
    threeParty()
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //workmanager
    val work_version = "2.7.1"
    implementation ("androidx.work:work-runtime:$work_version")
    implementation ("androidx.work:work-runtime-ktx:$work_version")
}