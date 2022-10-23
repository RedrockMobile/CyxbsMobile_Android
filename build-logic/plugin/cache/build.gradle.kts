
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

apply(from="../../script/plugin.module.gradle")