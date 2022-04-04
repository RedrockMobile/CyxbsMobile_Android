buildscript {
    apply(from= "$rootDir/build_logic/script/githook.gradle")
}

tasks.register("cache"){
    group = "publishing"
    subprojects
        .filter { it.name != "module_app" }
        .map { it.tasks.named("publishModuleCachePublicationToMavenRepository") }
        .run {
            dependsOn(this)
        }
}