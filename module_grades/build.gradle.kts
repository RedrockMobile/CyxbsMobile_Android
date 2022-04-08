import versions.defaultRoom
import versions.lottie
import versions.threeParty

plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(project(":lib_account:api_account"))
    lottie()
    defaultRoom()
    threeParty()
}
