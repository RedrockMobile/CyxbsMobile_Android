package script

plugins {
    `maven-publish`
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("moduleCache"){
                from(components["debug"])
            }

            repositories {
                maven{
                    url = uri("$rootDir/maven")
                    group = "com.mredrock.team"
                    name = name
                    version = "cache"
                }
            }
        }
    }
}
