import versions.*

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    implementation(project(":lib_protocol:api_protocol"))
    eventBus()
    dialog()
    paging()
    photoView()
    threeParty()
    lPhotoPicker()
    defaultRoom()
    implementation("com.google.android:flexbox:2.0.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.tencent.tauth:qqopensdk:3.52.0")
    implementation("com.github.MZCretin:ExpandableTextView:v1.6.1-x")
    implementation("com.umeng.umsdk:common:9.1.0")
    implementation("com.github.yalantis:ucrop:2.2.1")
}
android.buildFeatures.dataBinding = true
